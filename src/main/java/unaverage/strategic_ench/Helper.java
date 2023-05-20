package unaverage.strategic_ench;

import com.mojang.datafixers.util.Pair;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

import java.util.*;
import java.util.regex.PatternSyntaxException;

public class Helper {
    private static final Map<Pair<?, ?>, Object> cacheMap = new HashMap<>();

    /**
     * Gets the value that is configured to be associated with the item
     *
     * @param item The item that is being mapped to its value
     * @param configMap The configuration maps that contains all the mappings of item to value
     * @param registry The minecraft registry that contains all the id of the item
     * {@return The value mapped from the item, null if the item maps to no value, null if the registry is not yet complete}
     * @param <I> The type of the item
     * @param <O> The type of the value
     */
    public static <I, O> O getValueFromConfig(I item, Map<String, O> configMap, Registry<I> registry){
        var cacheKey = Pair.of(item, configMap);

        @SuppressWarnings("unchecked")
        var cached = (O)cacheMap.get(cacheKey);
        if (cached != null) return cached;

        var itemID = registry
            .getKey(item)
            .map(itemRegistryKey -> itemRegistryKey.getValue().toString())
            .orElse(null);

        if (itemID == null) return null;

        //Runs the loop twice
        //First time it checks for exact equality
        //Second time, it tests for regex
        for (var o: configMap.entrySet()){
            var testedID = o.getKey();
            var testedValue = o.getValue();

            if (itemID.equals(testedID)){
                cacheMap.put(cacheKey, testedValue);

                return testedValue;
            }
        }
        for (var o: configMap.entrySet()){
            var testedID = o.getKey();
            var testedValue = o.getValue();

            try {
                if (itemID.matches(testedID)) {
                    cacheMap.put(cacheKey, testedValue);

                    return testedValue;
                }
            }
            catch (PatternSyntaxException e){
                StrategicEnchanting.LOGGER.warn(testedID + " is not valid regex");
            }
        }

        cacheMap.put(Pair.of(item, configMap), null);
        return null;
    }

    public static <I> boolean contains(I item, Set<String> map, Registry<I> registry){
        var cachedKey = Pair.of(item, map);

        var cached = (Boolean)cacheMap.get(cachedKey);
        if (cached != null) return cached;

        var itemID = registry
            .getKey(item)
            .map(itemRegistryKey -> itemRegistryKey.getValue().toString())
            .orElse(null);

        if (itemID == null) return false;

        for (var testedID: map){
            if (itemID.equals(testedID)){
                cacheMap.put(Pair.of(item, map), true);

                return true;
            }
        }
        for (var id: map){
            try {
                if (itemID.matches(id)) {
                    cacheMap.put(cachedKey, true);

                    return true;
                }
            }
            catch (PatternSyntaxException e){
                StrategicEnchanting.LOGGER.warn(id + " is not valid regex");
            }
        }

        cacheMap.put(cachedKey, false);
        return false;
    }


    /**
     * Merges multiple comparators into one new comparator
     * If the first comparator returns zero, then it moves on to the second.
     * If the second comparator returns zero, then it moves on to the third. Et-cetera
     *
     * @param comparator The list of all comparators being merged into one
     * @return The combined comparator
     * @param <T> The type that the comparators compare
     */
    @SafeVarargs
    public static <T> Comparator<T> merge(Comparator<T>... comparator){
        return (o1, o2)->{
            for (Comparator<T> c: comparator){
                int result = c.compare(o1,o2);
                if (result != 0) return result;
            }
            return 0;
        };
    }
}
