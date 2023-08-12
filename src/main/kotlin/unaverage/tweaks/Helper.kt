@file:Suppress("FoldInitializerAndIfToElvis", "LiftReturnOrAssignment")

package unaverage.tweaks

import net.minecraft.block.BlockState
import net.minecraft.block.CropBlock
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.EntityType
import net.minecraft.entity.passive.AllayEntity
import net.minecraft.item.AirBlockItem
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.tag.ItemTags
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldView
import java.util.function.Predicate
import java.util.regex.PatternSyntaxException
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


private val cachedGetID: MutableMap<Any?, String> = HashMap()
fun <T> T.cachedGetID(r: Registry<T>): String {
    return cachedGetID.getOrPut(this){
        r
        .getKey(this)
        .map { itemRegistryKey -> itemRegistryKey.value.toString() }
        .get()
    }
}

private val cachedGet: MutableMap<String, Any?> = HashMap()
fun <T> Map<String, T>.cachedGet(itemID: String): T? {
    @Suppress("UNCHECKED_CAST")
    return cachedGet.getOrPut(itemID){
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


private val cachedContain: MutableMap<String, Boolean> = HashMap()
fun Set<String>.cachedContain(id: String): Boolean {
    return cachedContain.getOrPut(id){
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


fun <T> getFromId(id: String, registry: Registry<T>): T?{
    return registry.get(
        Identifier(
            id.split(':').getOrElse(0){ return null },
            id.split(':').getOrElse(1){ return null }
        )
    )
    .also {
        if (it == null) {
            UnaverageTweaks.logMissingID(id)
        }

        if (it is AirBlockItem && id != "minecraft:air") {
            UnaverageTweaks.logMissingID(id)
            return null
        }
    }
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

/**
 * {@return The total weights of enchantments the item can hold}
 * @param item The item being tested
 */
fun getCapacity(item: Item?): Double? {
    return GlobalConfig
        .EnchantmentCaps
        .item_capacities
        .cachedGet(
            item.cachedGetID(Registries.ITEM)
        )
}


/**
 * @param map The enchantment map being tested
 * {@return The total weight of all the enchantments in an enchantment map}
 */
fun getWeight(map: Map<Enchantment, Int>): Double {
    fun getWeight(e: Enchantment, level: Int): Double {
        val weightByID = GlobalConfig
            .EnchantmentCaps
            .enchantment_weights
            .cachedGet(
                e.cachedGetID(Registries.ENCHANTMENT)
            )

        if (weightByID != null && weightByID.size > level - 1) {
            //subtracts by 1 so that level 1 maps to index 0 and et-cetera
            return weightByID[level - 1]
        }

        val weightByMax = GlobalConfig
            .EnchantmentCaps
            .enchantment_weights
            .cachedGet(e.maxLevel.toString())

        val ratio: Double
        if (weightByMax != null && weightByMax.size > level - 1) {
            //subtracts by 1 so that level 1 maps to index 0 and et-cetera
            ratio = weightByMax[level - 1]
        }
        else {
            ratio = (level / e.maxLevel.toDouble())
        }

        if (e.isCursed) return -ratio

        return ratio
    }

    return map
        .map { (k, v)->getWeight(k, v) }
        .sum()
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
    cap: Double?,
    priority: Predicate<Enchantment>
) {
    //Returns whether the candidate is above the cap
    fun isOverCap(map: Map<Enchantment, Int>, cap: Double): Boolean {
        val weight = getWeight(map)
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
        return merge(
            //enchantments that keep more levels from prioritized enchantments should go first
            Comparator.comparing { it.entries.filter { (key, _) -> priority.test(key) }.sumOf { (_, value) -> value } },
            //candidates that keeps more treasure enchantments should be considered first
            Comparator.comparing { it.keys.stream().filter { obj -> obj.isTreasure }.count() },
            //permutations that keeps more kinds of enchantments should be considered first
            Comparator.comparing { it.keys.size },
            //candidates with more weight should be considered first
            Comparator.comparing { getWeight(it) },
            Comparator.comparing { it.values.sum() }
        )
    }

    if (cap == null || cap < 0) return

    if (!isOverCap(map, cap)) return

    val candidates = getAllCandidates(map, cap)
    if (candidates.isEmpty()) return

    val candidateComparator = getCandidateComparator(priority)
    candidates.sortWith(candidateComparator)

    val bestCandidate = candidates.last()

    map.clear()
    map.putAll(bestCandidate)
}


fun canPlaceCropAt(crop: BlockState, pos: BlockPos, world: WorldView): Boolean {
    return crop.canPlaceAt(world, pos) && world.getBlockState(pos).isAir
}

fun heldItemAsCropBlock(allay: AllayEntity): BlockState?{
    val item = allay.inventory.getStack(0)
    if (item == null) return null
    if (item.count < 0) return null

    if (!item.isIn(ItemTags.VILLAGER_PLANTABLE_SEEDS)) return null
    if (item.item !is BlockItem) return null

    val crop = (item.item as BlockItem).block as? CropBlock
    if (crop == null) return null

    return crop.withAge(0)
}

fun getNearestFarmPos(pos: BlockPos, cropBlock: BlockState, world: WorldView): BlockPos?{
    iterateSquare(5){ i,j,k->
        @Suppress("NAME_SHADOWING")
        val pos = BlockPos(
            pos.x + i,
            pos.y + j,
            pos.z + k
        )

        if (canPlaceCropAt(cropBlock, pos, world)){
            return pos
        }
    }
    return null
}

@Suppress("SameParameterValue")
private inline fun iterateSquare(maxRange: Int, fn: (Int, Int, Int)->Unit){
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

fun affectedByBaneOfArthropod(e: EntityType<*>): Boolean {
    return GlobalConfig.Miscellaneous.bane_of_arthropods_also_affects
        .toSet()
        .contains(
            e.cachedGetID(Registries.ENTITY_TYPE)
        )
}

fun enchantmentIsBlacklisted(e: Enchantment): Boolean {
    return GlobalConfig.Miscellaneous.enchantment_blacklist
        .toSet()
        .cachedContain(
            e.cachedGetID(Registries.ENCHANTMENT)
        )
}

fun fireProtectionHasLavaDuration(): Boolean{
    return GlobalConfig.Miscellaneous.fire_protection_lava_immunity != 0.0
}

fun getFireProtectionLavaImmunityDuration(level: Int): Int {
    return GlobalConfig.Miscellaneous.fire_protection_lava_immunity
        .let { it * level }
        .let{ it * 20 }
        .roundToInt()
}

fun fireProtectionProtectsAgainst(e: EntityType<*>): Boolean {
    return GlobalConfig.Miscellaneous.fire_protection_protects_against
        .toSet()
        .cachedContain(
            e.cachedGetID(Registries.ENTITY_TYPE),
        )
}

fun getNewAnimalFeedList(e: EntityType<*>): List<Item>?{
    @Suppress("ReplaceGetOrSet")
    return GlobalConfig.Miscellaneous.animals_eat
        .get(
            e.cachedGetID(Registries.ENTITY_TYPE)
        )
        ?.mapNotNull {
            getFromId(it, Registries.ITEM)
        }
}

fun healedWhenEat(e: EntityType<*>): Boolean{
    return GlobalConfig
        .Miscellaneous
        .animals_heal_when_eat
        .contains(
            e.cachedGetID(Registries.ENTITY_TYPE)
        )
}