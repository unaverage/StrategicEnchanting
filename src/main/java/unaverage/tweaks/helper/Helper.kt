@file:Suppress("FoldInitializerAndIfToElvis", "LiftReturnOrAssignment")

package unaverage.tweaks.helper

import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.EntityType
import net.minecraft.item.AirBlockItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import unaverage.tweaks.GlobalConfig
import unaverage.tweaks.UnaverageTweaks
import java.util.*
import java.util.function.Predicate
import java.util.regex.PatternSyntaxException
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.absoluteValue

private val cachedGetID: MutableMap<Any?, String> = HashMap()
fun <T> T.getID(r: Registry<T>): String {
    return cachedGetID.getOrPut(this){
        r
        .getKey(this)
        .map { itemRegistryKey -> itemRegistryKey.value.toString() }
        .get()
    }
}

private val cachedGet: MutableMap<Pair<Map<String, *>, String>, Any?> = HashMap()
fun <T> Map<String, T>.getWithRegex(itemID: String): T? {
    @Suppress("UNCHECKED_CAST")
    return cachedGet.getOrPut(this to itemID){
        //Runs the loop twice
        //First time it checks for exact equality
        //Second time, it tests for regex
        for ((testedID, testedValue) in this) {
            if (itemID == testedID) {
                return@getOrPut testedValue
            }
        }
        for ((testedID, testedValue) in this) {
            try {
                if (itemID.matches(Regex(testedID))) {
                    return@getOrPut testedValue
                }
            } catch (e: PatternSyntaxException) {
                UnaverageTweaks.LOGGER.warn("$testedID is not valid regex")
            }
        }
        null
    } as T?
}


private val cachedContain: MutableMap<Pair<Set<String>, String>, Boolean> = HashMap()
fun Set<String>.containsRegex(id: String): Boolean {
    return cachedContain.getOrPut(this to id){
        for (testedID in this) {
            if (id == testedID) {
                return@getOrPut true
            }
        }
        for (testedID in this) {
            try {
                if (id.matches(Regex(testedID))) {
                    return@getOrPut true
                }
            } catch (e: PatternSyntaxException) {
                UnaverageTweaks.LOGGER.warn("$id is not valid regex")
            }
        }
        false
    }
}

fun <T> String.fromId(registry: Registry<T>): T?{
    return registry.get(
        Identifier(
            split(':').getOrElse(0){ return null },
            split(':').getOrElse(1){ return null }
        )
    )
    .also {
        if (it == null) {
            UnaverageTweaks.logMissingID(this)
        }

        if (it is AirBlockItem && this != "minecraft:air") {
            UnaverageTweaks.logMissingID(this)
            return null
        }
    }
}


/**
 * {@return The total weight of all the enchantments in an enchantment map}
 */
val Map<Enchantment, Int>.totalWeight: Double
    get() {
        fun getWeight(e: Enchantment, level: Int): Double {

            run{
                val weightByID = GlobalConfig.tools_have_limited_enchantment_capacity.enchantment_weights_by_id
                    .getWithRegex(
                        e.getID(Registries.ENCHANTMENT)
                    )
                    ?.get(level.toString())

                if (weightByID == null) return@run

                return weightByID
            }

            run{
                val weightByMax = GlobalConfig.tools_have_limited_enchantment_capacity.enchantment_weights_by_max_levels
                    .get(
                        e.maxLevel.toString()
                    )
                    ?.get(
                        level.toString()
                    )

                if (weightByMax == null) return@run

                if (e.isCursed) return -weightByMax

                return weightByMax
            }

            run{
                val weightByDefault = (level / e.maxLevel.toDouble())

                if (e.isCursed) return -weightByDefault

                return weightByDefault
            }
        }

        return map { (k, v) -> getWeight(k, v) }
            .sum()
    }

/**
 * Mutates an enchantment map and adjusts the levels of each enchantments so that the total weight is less than or equal to the capacity
 *
 * @param cap The capacity that the total weight of the enchantment is being adjusted to
 * @param priority Which enchantments are prioritized to be preserved
 */
fun MutableMap<Enchantment, Int>.cap(
    cap: Double?,
    priority: (Enchantment)->Boolean = {false}
) {
    //Returns whether the candidate is above the cap
    fun isOverCap(map: Map<Enchantment, Int>, cap: Double): Boolean {
        val weight = map.totalWeight
        val epsilon = .001

        //adds a small epsilon to avoid slight inaccuracies
        return weight > cap + epsilon
    }

    //Generates all possible outcomes that an enchantment map can be reduced to fit below a cap
    fun getAllCandidates(map: Map<Enchantment, Int>, cap: Double): ArrayList<Map<Enchantment, Int>> {
        //Gets the max number of candidates possible
        fun maxCandidateIndex(map: Map<Enchantment, Int>): Int {
            return map
            .values
            .map { v -> v + 1 }
            .fold(1) { a , b -> a * b }
        }

        //Generates a candidate from an index number
        fun getCandidateFromIndex(map: Map<Enchantment, Int>, index: Int): Map<Enchantment, Int> {
            @Suppress("NAME_SHADOWING")
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

        val candidates = ArrayList<Map<Enchantment, Int>>()
        for (i in 0 until maxCandidateIndex(map)) {
            val candidate = getCandidateFromIndex(map, i)
            if (isOverCap(candidate, cap)) continue
            candidates.add(candidate)
        }
        return candidates
    }

    //Returns a comparator that compares candidates
    fun getCandidateComparator(priority: Predicate<Enchantment>): Comparator<Map<Enchantment, Int>> {
        fun <T> merge(vararg comparator: Comparator<T>): Comparator<T> {
            return Comparator { o1: T, o2: T ->
                for (c in comparator) {
                    val result = c.compare(o1, o2)
                    if (result != 0) return@Comparator result
                }
                0
            }
        }

        return merge(
            //enchantments that keep more levels from prioritized enchantments should go first
            Comparator.comparing { it.entries.filter { (key, _) -> priority.test(key) }.sumOf { (_, value) -> value } },
            //candidates that keeps more treasure enchantments should be considered first
            Comparator.comparing { it.keys.stream().filter { obj -> obj.isTreasure }.count() },
            //permutations that keeps more kinds of enchantments should be considered first
            Comparator.comparing { it.keys.size },
            //candidates with more weight should be considered first
            Comparator.comparing { it.totalWeight },
            Comparator.comparing { it.values.sum() }
        )
    }

    if (cap == null || cap < 0) return

    if (!isOverCap(this, cap)) return

    val candidates = getAllCandidates(this, cap)
    if (candidates.isEmpty()) return

    val candidateComparator = getCandidateComparator(priority)
    candidates.sortWith(candidateComparator)

    val bestCandidate = candidates.last()

    clear()
    putAll(bestCandidate)
}


val EntityType<*>.newFeedList: List<Item>?
    get() {
        return GlobalConfig.animals_have_custom_feeding.animals_affected
            .getWithRegex(
                this.getID(Registries.ENTITY_TYPE)
            )
            ?.mapNotNull {
                it.fromId(Registries.ITEM)
            }
    }

fun passesChance(chance: Double, rng: Random): Boolean{
    val r = rng.nextDouble()

    return chance > r
}

@Suppress("SameParameterValue")
inline fun iterateCube(maxRange: Int, fn: (x:Int, y:Int, z:Int)->Unit){
    for (r in 0..maxRange) {
        for (i in -r..r) {
            for (j in -r..r) {
                for (k in -r..r) {
                    if (i.absoluteValue == r || j.absoluteValue == r || k.absoluteValue == r) {
                        fn(i,j,k)
                    }
                }
            }
        }
    }
}

