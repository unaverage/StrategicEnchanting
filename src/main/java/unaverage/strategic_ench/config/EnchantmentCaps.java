package unaverage.strategic_ench.config;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigUtil;
import com.typesafe.config.ConfigValueType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import unaverage.strategic_ench.Helper;
import unaverage.strategic_ench.StrategicEnchanting;

import java.util.*;
import java.util.function.Predicate;

/**
 * Contains configuration values relating to the capacities of items and the weight of enchantments
 */
public class EnchantmentCaps {

    static Map<String, Object> getDefault(boolean hasFile) {
        return Map.of(
            Round_2_3rd_to_Half.class.getSimpleName(), Round_2_3rd_to_Half.DEFAULT,
            Round_3_5th_to_Half.class.getSimpleName(), Round_3_5th_to_Half.DEFAULT,
            ItemCapacities.class.getSimpleName(), ItemCapacities.getDefault(hasFile),
            CustomWeights.class.getSimpleName(), CustomWeights.getDefault(hasFile)
        );
    }

    final Round_2_3rd_to_Half round_2_3rd_to_half;
    final Round_3_5th_to_Half round_3_5th_to_half;
    final ItemCapacities itemCapacities;
    final CustomWeights customWeights;

    EnchantmentCaps(Config c) {
        c = c.getConfig(this.getClass().getSimpleName());

        round_2_3rd_to_half = new Round_2_3rd_to_Half(c);
        round_3_5th_to_half = new Round_3_5th_to_Half(c);
        itemCapacities = new ItemCapacities(c);
        customWeights = new CustomWeights(c);
    }

    /**
     * Mutates an enchantment map and adjusts the levels of each enchantments so that the total weight is less than or equal to the capacity
     *
     * @param map The enchantment to level map being mutated
     * @param cap The capacity that the total weight of the enchantment is being adjusted to
     * @param priority Which enchantments are prioritized to be preserved
     */
    public void capEnchantmentMap(
        Map<Enchantment, Integer> map,
        double cap,
        Predicate<Enchantment> priority
    ){
        if (cap < 0) return;

        if (!isOverCap(map, cap)) return;

        var candidates = getAllCandidates(map, cap);
        if (candidates.isEmpty()) return;

        var candidateComparator = getCandidateComparator(priority);
        candidates.sort(candidateComparator);

        var bestCandidate = candidates.get(0);

        map.clear();
        map.putAll(bestCandidate);
    }

    /**
     * {@return The total weights of enchantments the item can hold}
     * @param item The item being tested
     */
    public double getCapacity(Item item){
        var result = Helper.getValueFromConfig(
            item,
            itemCapacities.map,
            Registry.ITEM
        );

        if (result == null) return -1;

        return result.doubleValue();
    }

    /**
     * @param map The enchantment map being tested
     * {@return The total weight of all the enchantments in an enchantment map}
     */
    public double getWeight(Map<Enchantment, Integer> map) {
        return map.entrySet()
            .stream()
            .map(o->getWeight(o.getKey(),o.getValue()))
            .reduce(0.0, Double::sum);
    }

    /**
     * {@return The specific weight of this enchantments and its level}
     * @param e The enchantment being tested
     * @param level The enchantment level
     */
    private double getWeight(Enchantment e, int level){
        var custom = Helper.getValueFromConfig(
            e,
            this.customWeights.value,
            Registry.ENCHANTMENT
        );

        if (custom != null && custom.size() > level-1) {
            //subtracts by 1 so that level 1 maps to index 0 and et-cetera
            return custom.get(level-1).doubleValue();
        }

        var ratio = level/(double)e.getMaxLevel();

        if (roundsToHalf(level, e.getMaxLevel())){
            ratio = 0.5;
        }

        if (e.isCursed()){
            return -ratio;
        }

        return ratio;
    }


    /**
     * Returns true if an enchantment's weight can be rounded to one half
     */
    private boolean roundsToHalf(int level, int maxLevel){
        if (level == 2 && maxLevel == 3 && round_2_3rd_to_half.value) return true;

        if (level == 3 && maxLevel == 5 && round_3_5th_to_half.value) return true;

        return false;
    }

    /**
     * Generates all possible outcomes that an enchantment map can be reduced to fit below a cap
     */
    private ArrayList<Map<Enchantment, Integer>> getAllCandidates(Map<Enchantment, Integer> map, double cap) {
        var candidates = new ArrayList<Map<Enchantment,Integer>>();
        for (var i = 0; i < maxCandidateIndex(map); i++){
            var candidate = getCandidateFromIndex(map, i);

            if (isOverCap(candidate, cap)) continue;

            candidates.add(candidate);
        }
        return candidates;
    }

    /**
     * Compares two condidates and returns the most preferable candidate of the two
     */
    private Comparator<Map<Enchantment, Integer>> getCandidateComparator(Predicate<Enchantment> priority) {
        return Helper.merge(
            //enchantments that keep more levels from prioritized enchantments should go first
            Comparator.comparing(
                e->-e.entrySet().stream().filter(o->priority.test(o.getKey())).map(o->o.getValue()).reduce(0, Integer::sum)
            ),
            //candidates that keeps more treasure enchantments should be considered first
            Comparator.comparing(
                e->-e.keySet().stream().filter(Enchantment::isTreasure).count()
            ),
            //permutations that keeps more enchantments should be considered first
            Comparator.comparing(
                e->-e.keySet().size()
            ),
            //candidates with more weight should be considered first
            Comparator.comparing(
                e->-getWeight(e)
            ),
            Comparator.comparing(
                e->-e.values().stream().reduce(0, Integer::sum)
            )
        );
    }

    /**
     * Gets the max number of candidates possible
     */
    private int maxCandidateIndex(Map<Enchantment, Integer> map){
        return map.values().stream()
            .map(v->v+1)
            .reduce(1, (a, b)->a*b);
    }

    /**
     * Generates a candidate from an index number
     */
    private Map<Enchantment,Integer> getCandidateFromIndex(Map<Enchantment, Integer> map, int index){
        Map<Enchantment, Integer> result = new HashMap<>();

        //sorts the enchantment alphabetically so that this function can return a consistent candidate
        List<Enchantment> order = map
            .keySet()
            .stream()
            .sorted(
                Comparator.comparing(Enchantment::getTranslationKey)
            ).toList();

        for (Enchantment e: order){
            int level = index % (map.get(e)+1);

            if (level != 0){
                result.put(e, level);
            }

            index /= map.get(e)+1;
        }

        return result;
    }

    /**
     * Returns whether the candidate is above the cap
     */
    private boolean isOverCap(Map<Enchantment,Integer> map, double cap){
        var weight = getWeight(map);

        var epsilon = .001;

        //adds a small epsilon to avoid slight inaccuracies
        return weight > cap+epsilon;
    }

    static class ItemCapacities {
        static Map<String, Number> getDefault(boolean newFile){
            if (!newFile) return Map.of();

            return new ImmutableMap.Builder<String, Number>()
                .put("minecraft\":\"wooden_\".+\"", -1)
                .put("minecraft\":\"leather_\".+\"", -1)
                .put("minecraft\":\"stone_\".+\"", -1)
                .put("minecraft\":\"golden_\".+\"", -1)
                .put("minecraft\":\"chainmail_\".+\"", 3)
                .put("minecraft\":\"iron_\".+\"", 3)
                .put("minecraft\":\"diamond_\".+\"", 2)
                .put("minecraft\":\"netherite_\".+\"", 1.5)
                .put("minecraft\":\"bow", 2.5)
                .put("minecraft\":\"crossbow", 2.5)
                .put("minecraft\":\"elytra", 2)
                .put("minecraft\":\"shears", 2)
                .put("minecraft\":\"enchanted_book", -1)
                //endregion

                .build();
        }
        Map<String, Number> map;

        ItemCapacities(Config c) {
            map = new HashMap<>();

            c.getConfig(this.getClass().getSimpleName())
                .entrySet()
                .stream()
                .forEach(
                    o->{
                        var id = ConfigUtil.splitPath(o.getKey()).get(0);
                        var valueWrapped = o.getValue();

                        Number value;
                        if (valueWrapped.valueType() != ConfigValueType.NUMBER){
                            StrategicEnchanting.LOGGER.warn(id + " is not associated with a number");
                            return;
                        }
                        value = (Number)valueWrapped.unwrapped();

                        map.put(id, value);
                    }
                );
        }
    }

    static class CustomWeights{
        static Map<String,List<Number>> getDefault(boolean newFile){
            if (!newFile) return Map.of();

            return Map.of("modid\":\"example_enchantment", List.of(0.25, 0.5, 0.75, 1.0));
        }

        final Map<String,List<Number>> value;

        CustomWeights(Config c){
            c = c.getConfig(this.getClass().getSimpleName());

            value = new HashMap<>();

            c.entrySet().forEach(
                o->{
                    var id = ConfigUtil.splitPath(o.getKey()).get(0);

                    var listUnwrapped =  o.getValue();

                    if (listUnwrapped.valueType() != ConfigValueType.LIST){
                        StrategicEnchanting.LOGGER.warn(id + " is not associated with a list");
                        return;
                    }

                    var list = (List<?>)listUnwrapped.unwrapped();
                    for (var v: list){
                        if (!(v instanceof Number)){
                            StrategicEnchanting.LOGGER.warn(v + " in " + id + "is not a number");
                            return;
                        }
                    }

                    //noinspection unchecked
                    value.put(
                        id,
                        (List<Number>)list
                    );
                }
            );
        }
    }

    static class Round_2_3rd_to_Half {
        static final boolean DEFAULT = true;

        boolean value;

        Round_2_3rd_to_Half(Config c) {
            value = c.getBoolean(this.getClass().getSimpleName());
        }
    }

    static class Round_3_5th_to_Half {
        static final boolean DEFAULT = true;

        boolean value;

        Round_3_5th_to_Half(Config c) {
            value = c.getBoolean(this.getClass().getSimpleName());
        }
    }
}
