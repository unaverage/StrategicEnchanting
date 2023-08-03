package unaverage.strategic_ench

import com.mojang.datafixers.util.Pair
import java.util.regex.PatternSyntaxException

object Helper {
    private val cacheMap: MutableMap<Pair<*, *>, Any?> = HashMap()

    /**
     * Gets the value that is configured to be associated with the item
     *
     * @param item The item that is being mapped to its value
     * @param configMap The configuration maps that contains all the mappings of item to value
     * @param registry The minecraft registry that contains all the id of the item
     * {@return The value mapped from the item, null if the item maps to no value, null if the registry is not yet complete}
     * @param <I> The type of the item
     * @param <O> The type of the value
    </O></I> */
    fun <I, O> getValueFromConfig(item: I, configMap: Map<String, O>, itemID: String): O? {
        val cacheKey = Pair.of(item, configMap)
        val cached = cacheMap[cacheKey] as O?
        if (cached != null) return cached

        //Runs the loop twice
        //First time it checks for exact equality
        //Second time, it tests for regex
        for ((testedID, testedValue) in configMap) {
            if (itemID == testedID) {
                cacheMap[cacheKey] = testedValue
                return testedValue
            }
        }

        for ((testedID, testedValue) in configMap) {
            try {
                if (itemID.matches(Regex(testedID))) {
                    cacheMap[cacheKey] = testedValue
                    return testedValue
                }
            } catch (e: PatternSyntaxException) {
                StrategicEnchanting.LOGGER.warn("$testedID is not valid regex")
            }
        }
        cacheMap[Pair.of(item, configMap)] = null
        return null
    }

    fun <I> contains(item: I, map: Set<String>, itemID: String): Boolean {
        val cachedKey = Pair.of(item, map)
        val cached = cacheMap[cachedKey] as Boolean?
        if (cached != null) return cached


        for (testedID in map) {
            if (itemID == testedID) {
                cacheMap[Pair.of(item, map)] = true
                return true
            }
        }
        for (id in map) {
            try {
                if (itemID.matches(Regex(id))) {
                    cacheMap[cachedKey] = true
                    return true
                }
            } catch (e: PatternSyntaxException) {
                StrategicEnchanting.LOGGER.warn("$id is not valid regex")
            }
        }
        cacheMap[cachedKey] = false
        return false
    }

    /**
     * Merges multiple comparators into one new comparator
     * If the first comparator returns zero, then it moves on to the second.
     * If the second comparator returns zero, then it moves on to the third. Et-cetera
     *
     * @param comparator The list of all comparators being merged into one
     * @return The combined comparator
     * @param <T> The type that the comparators compare
    </T> */
    @SafeVarargs
    fun <T> merge(vararg comparator: Comparator<T>): Comparator<T> {
        return Comparator { o1: T, o2: T ->
            for (c in comparator) {
                val result = c.compare(o1, o2)
                if (result != 0) return@Comparator result
            }
            0
        }
    }
}