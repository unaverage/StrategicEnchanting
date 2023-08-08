package unaverage.tweaks.config

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.EntityType
import net.minecraft.registry.Registries
import unaverage.tweaks.UnaverageTweaks
import unaverage.tweaks.cachedContain
import unaverage.tweaks.cachedGetID
import unaverage.tweaks.config.Config.Companion.overwriteFromFile
import unaverage.tweaks.config.Config.Companion.writeToFile
import kotlin.math.roundToInt

const val FILE_NAME = UnaverageTweaks.MOD_ID + ".json"

fun affectedByBaneOfAnthropod(e: EntityType<*>): Boolean {
    return GlobalConfig
        .Miscellaneous
        .bane_of_arthropods_also_affects
        .toSet()
        .contains(
            e.cachedGetID(Registries.ENTITY_TYPE)
        )
}

fun enchantmentIsBlacklisted(e: Enchantment): Boolean {
    return GlobalConfig
        .Miscellaneous
        .blacklist_enchantments
        .toSet()
        .cachedContain(
            e.cachedGetID(Registries.ENCHANTMENT),
        )
}

fun fireProtectionHasLavaDuration(): Boolean{
    return GlobalConfig.Miscellaneous.fire_protection_grants_seconds_of_lava_immunity_per_level != 0.0
}

fun getFireProtectionLavaImmunityDuration(level: Int): Int {
    return GlobalConfig
        .Miscellaneous
        .fire_protection_grants_seconds_of_lava_immunity_per_level
        .let { it * level }
        .let{ it * 20 }
        .roundToInt()
}

fun fireProtectionProtectsAgainst(e: EntityType<*>): Boolean {
    return GlobalConfig
        .Miscellaneous
        .fire_protection_protects_against
        .toSet()
        .cachedContain(
            e.cachedGetID(Registries.ENTITY_TYPE),
        )
}

fun runGlobalConfig() {
    val file = FabricLoader.getInstance().configDir.resolve(FILE_NAME).toFile()

    if (!file.exists()){
        GlobalConfig.writeToFile(file)
    }
    else {
        GlobalConfig.overwriteFromFile(
            file,
            { UnaverageTweaks.logInvalidConfig(it)},
            { UnaverageTweaks.logNonExistentConfig(it)}
        )
        GlobalConfig.writeToFile(file)
    }
}

object GlobalConfig: Config{
    object EnchantmentCaps: Config{
        @JvmField
        var enchantment_weights = mapOf(
            "1" to listOf(1.0),
            "2" to listOf(.5, 1.0),
            "3" to listOf(.25, .5, 1.0),
            "4" to listOf(.25, .5, .75, 1.0),
            "5" to listOf(.25, .25, .5, .75, 1.0),
            "modid:example" to listOf(0.25, .5, .75, 1.0),
        )

        @JvmField
        var item_capacities = mapOf(
            "minecraft:bow" to 2.5,
            "minecraft:chainmail_.+" to 3.0,
            "minecraft:crossbow" to 2.5,
            "minecraft:diamond_.+" to 2.0,
            "minecraft:elytra" to 2.0,
            "minecraft:enchanted_book" to null,
            "minecraft:golden_.+" to null,
            "minecraft:iron_.+" to 3.0,
            "minecraft:leather_.+" to null,
            "minecraft:netherite_.+" to 1.5,
            "minecraft:shears" to 2.0,
            "minecraft:stone_.+" to null,
            "minecraft:wooden_.+" to null,
        )
    }
    
    object Miscellaneous: Config{
        @JvmField
        var mobs_can_cross_rails = true

        @JvmField
        var creepers_avoid_cats_at = 16

        @JvmField
        var pigs_eat = listOf(
            "minecraft:apple",
            "minecraft:beet_root",
            "minecraft:carrot",
            "minecraft:wheat",
        )

        @JvmField
        var animals_heal_when_eat = true

        @JvmField
        var bane_of_arthropods_also_affects = listOf("minecraft:guardian, minecraft:elder_guardian")

        @JvmField
        var blacklist_enchantments = listOf("minecraft:protection")

        @JvmField
        var fire_protection_protects_against = listOf("minecraft:blazed", "minecraft:magma_cube")

        @JvmField
        var fire_protection_grants_seconds_of_lava_immunity_per_level = 1.0

        @JvmField
        var thorn_no_longer_wears_down_armor = true

        //@JvmField
        //var zombies_are_more_knockback_resistant = true

        @JvmField
        var village_golems_only_fight_threats = true

        @JvmField
        var ridden_pigs_speed_multiplied_by = 2.0
        
        @JvmField
        var shields_no_longer_prevent_knockback = true

        @JvmField
        var frost_walker_melts_at_night = true

        @JvmField
        var allay_can_plant_crops = true
    }
}