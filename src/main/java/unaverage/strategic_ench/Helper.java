package unaverage.strategic_ench;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.registry.Registry;

import java.util.*;
import java.util.regex.PatternSyntaxException;

public class Helper {
    private static final Map<Pair<Object, Object>, Object> cacheMap = new HashMap<>();

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
        @SuppressWarnings("unchecked")
        var cached = (O)cacheMap.get(Pair.of(item, configMap));
        if (cached != null) return cached;

        var id = registry
            .getKey(item)
            .map(itemRegistryKey -> itemRegistryKey.getValue().toString())
            .orElse(null);

        if (id == null) return null;

        //Runs the loop twice
        //First time it checks for exact equality
        //Second time, it tests for regex
        for (var o: configMap.entrySet()){
            var testedKey = o.getKey();
            var testedValue = o.getValue();

            if (id.equals(testedKey)){
                cacheMap.put(Pair.of(item, configMap), testedValue);

                return testedValue;
            }
        }
        for (var o: configMap.entrySet()){
            var testedKey = o.getKey();
            var testedValue = o.getValue();

            try {
                if (id.matches(testedKey)) {
                    cacheMap.put(Pair.of(item, configMap), testedValue);

                    return testedValue;
                }
            }
            catch (PatternSyntaxException e){
                StrategicEnchanting.LOGGER.warn(testedKey + " is not valid regex");
            }
        }

        cacheMap.put(Pair.of(item, configMap), null);
        return null;
    }

    public static <I> boolean contains(I item, Set<String> map, Registry<I> registry){
        @SuppressWarnings("unchecked")
        var cached = (Boolean)cacheMap.get(Pair.of(item, map));
        if (cached != null) return cached;

        var id = registry
            .getKey(item)
            .map(itemRegistryKey -> itemRegistryKey.getValue().toString())
            .orElse(null);

        if (id == null) return false;

        for (var key: map){
            if (id.equals(key)){
                cacheMap.put(Pair.of(item, map), true);

                return true;
            }
        }
        for (var key: map){
            try {
                if (id.matches(key)) {
                    cacheMap.put(Pair.of(item, map), true);

                    return true;
                }
            }
            catch (PatternSyntaxException e){
                StrategicEnchanting.LOGGER.warn(key + " is not valid regex");
            }
        }

        cacheMap.put(Pair.of(item, map), false);
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
