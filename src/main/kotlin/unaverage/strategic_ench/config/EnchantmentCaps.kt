package unaverage.strategic_ench.config

import com.google.common.collect.ImmutableMap
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.Item
import unaverage.strategic_ench.Helper
import unaverage.strategic_ench.config.EnchantmentCaps.CustomWeights
import unaverage.strategic_ench.config.EnchantmentCaps.ItemCapacities
import unaverage.strategic_ench.config.EnchantmentCaps.Round_2_3rd_to_Half
import unaverage.strategic_ench.config.EnchantmentCaps.Round_3_5th_to_Half
import java.util.function.Predicate

/**
 * Contains configuration values relating to the capacities of items and the weight of enchantments
 */
class EnchantmentCaps internal constructor() {
    val round_2_3rd_to_half: Round_2_3rd_to_Half
    val round_3_5th_to_half: Round_3_5th_to_Half
    val itemCapacities: ItemCapacities
    val customWeights: CustomWeights

    init {
        round_2_3rd_to_half = Round_2_3rd_to_Half()
        round_3_5th_to_half = Round_3_5th_to_Half()
        itemCapacities = ItemCapacities()
        customWeights = CustomWeights()
    }

    /**
     * Mutates an enchantment map and adjusts the levels of each enchantments so that the total weight is less than or equal to the capacity
     *
     * @param map The enchantment to level map being mutated
     * @param cap The capacity that the total weight of the enchantment is being adjusted to
     * @param priority Which enchantments are prioritized to be preserved
     */
    fun capEnchantmentMap(
        map: MutableMap<Enchantment, Int>,
        cap: Double,
        priority: Predicate<Enchantment>
    ) {
        if (cap < 0) return
        if (!isOverCap(map, cap)) return
        val candidates = getAllCandidates(map, cap)
        if (candidates.isEmpty()) return
        val candidateComparator = getCandidateComparator(priority)
        candidates.sortWith(candidateComparator)
        val bestCandidate = candidates[0]
        map.clear()
        map.putAll(bestCandidate)
    }

    /**
     * {@return The total weights of enchantments the item can hold}
     * @param item The item being tested
     */
    fun getCapacity(item: Item?): Double {
        return 2.0 ?: return (-1).toDouble()
    }

    /**
     * @param map The enchantment map being tested
     * {@return The total weight of all the enchantments in an enchantment map}
     */
    fun getWeight(map: Map<Enchantment, Int>): Double {
        return map.entries
            .stream()
            .map { (key, value): Map.Entry<Enchantment, Int> -> getWeight(key, value) }
            .reduce(0.0) { a: Double, b: Double -> java.lang.Double.sum(a, b) }
    }

    /**
     * {@return The specific weight of this enchantments and its level}
     * @param e The enchantment being tested
     * @param level The enchantment level
     */
    private fun getWeight(e: Enchantment, level: Int): Double {
        val custom = ArrayList<Number>()
        if (custom != null && custom.size > level - 1) {
            //subtracts by 1 so that level 1 maps to index 0 and et-cetera
            return custom[level - 1].toDouble()
        }
        var ratio = level / e.maxLevel.toDouble()
        if (roundsToHalf(level, e.maxLevel)) {
            ratio = 0.5
        }
        return if (e.isCursed) {
            -ratio
        } else ratio
    }

    /**
     * Returns true if an enchantment's weight can be rounded to one half
     */
    private fun roundsToHalf(level: Int, maxLevel: Int): Boolean {
        if (level == 2 && maxLevel == 3 && round_2_3rd_to_half.value) return true
        return if (level == 3 && maxLevel == 5 && round_3_5th_to_half.value) true else false
    }

    /**
     * Generates all possible outcomes that an enchantment map can be reduced to fit below a cap
     */
    private fun getAllCandidates(map: Map<Enchantment, Int>, cap: Double): ArrayList<Map<Enchantment, Int>> {
        val candidates = ArrayList<Map<Enchantment, Int>>()
        for (i in 0 until maxCandidateIndex(map)) {
            val candidate = getCandidateFromIndex(map, i)
            if (isOverCap(candidate, cap)) continue
            candidates.add(candidate)
        }
        return candidates
    }

    /**
     * Compares two condidates and returns the most preferable candidate of the two
     */
    private fun getCandidateComparator(priority: Predicate<Enchantment>): Comparator<Map<Enchantment, Int>> {
        return Helper.merge( //enchantments that keep more levels from prioritized enchantments should go first
            Comparator.comparing { e: Map<Enchantment, Int> ->
                -e.entries.stream().filter { (key): Map.Entry<Enchantment, Int> ->
                    priority.test(
                        key
                    )
                }.map { (_, value): Map.Entry<Enchantment, Int> -> value }
                    .reduce(0) { a: Int, b: Int -> Integer.sum(a, b) }
            },  //candidates that keeps more treasure enchantments should be considered first
            Comparator.comparing { e: Map<Enchantment, Int> ->
                -e.keys.stream().filter { obj: Enchantment -> obj.isTreasure }
                    .count()
            },  //permutations that keeps more enchantments should be considered first
            Comparator.comparing { e: Map<Enchantment, Int> -> -e.keys.size },  //candidates with more weight should be considered first
            Comparator.comparing { e: Map<Enchantment, Int> -> -getWeight(e) },
            Comparator.comparing { e: Map<Enchantment, Int> ->
                -e.values.stream().reduce(0) { a: Int, b: Int -> Integer.sum(a, b) }
            }
        )
    }

    /**
     * Gets the max number of candidates possible
     */
    private fun maxCandidateIndex(map: Map<Enchantment, Int>): Int {
        return map.values.stream()
            .map { v: Int -> v + 1 }
            .reduce(1) { a: Int, b: Int -> a * b }
    }

    /**
     * Generates a candidate from an index number
     */
    private fun getCandidateFromIndex(map: Map<Enchantment, Int>, index: Int): Map<Enchantment, Int> {
        var index = index
        val result: MutableMap<Enchantment, Int> = HashMap()

        //sorts the enchantment alphabetically so that this function can return a consistent candidate
        val order = map
            .keys
            .stream()
            .sorted(
                Comparator.comparing { obj: Enchantment -> obj.translationKey }
            ).toList()
        for (e in order) {
            val level = index % (map[e]!! + 1)
            if (level != 0) {
                result[e] = level
            }
            index /= map[e]!! + 1
        }
        return result
    }

    /**
     * Returns whether the candidate is above the cap
     */
    private fun isOverCap(map: Map<Enchantment, Int>, cap: Double): Boolean {
        val weight = getWeight(map)
        val epsilon = .001

        //adds a small epsilon to avoid slight inaccuracies
        return weight > cap + epsilon
    }

    class ItemCapacities {
        var map: Map<String, Number>

        init {
            map = HashMap()
        }

        companion object {
            fun getDefault(newFile: Boolean): Map<String, Number> {
                return if (!newFile) java.util.Map.of() else ImmutableMap.Builder<String, Number>()
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
                    .put("minecraft\":\"enchanted_book", -1) //endregion
                    .build()
            }
        }
    }

    class CustomWeights {
        val value: Map<String, List<Number>>

        init {
            value = HashMap()
        }

        companion object {
            fun getDefault(newFile: Boolean): Map<String, List<Number>> {
                return if (!newFile) java.util.Map.of() else java.util.Map.of(
                    "modid\":\"example_enchantment",
                    java.util.List.of<Number>(0.25, 0.5, 0.75, 1.0)
                )
            }
        }
    }

    class Round_2_3rd_to_Half {
        var value: Boolean

        init {
            value = DEFAULT
        }

        companion object {
            const val DEFAULT = true
        }
    }

    class Round_3_5th_to_Half {
        var value = true

        companion object {
            const val DEFAULT = true
        }
    }

    companion object {
        fun getDefault(hasFile: Boolean): Map<String, Any> {
            return java.util.Map.of(
                Round_2_3rd_to_Half::class.java.simpleName, Round_2_3rd_to_Half.DEFAULT,
                Round_3_5th_to_Half::class.java.simpleName, Round_3_5th_to_Half.DEFAULT,
                ItemCapacities::class.java.simpleName, ItemCapacities.getDefault(hasFile),
                CustomWeights::class.java.simpleName, CustomWeights.getDefault(hasFile)
            )
        }
    }
}