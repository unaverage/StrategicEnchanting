package unaverage.tweaks

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
import net.fabricmc.loader.api.FabricLoader
import roland_a.simple_configs.Config
import roland_a.simple_configs.Config.Companion.override
import roland_a.simple_configs.Config.Companion.toMap
import roland_a.simple_configs.InvalidValueException
import java.io.File

const val FILE_NAME = UnaverageTweaks.MOD_ID + ".json"

fun runGlobalConfig() {
    if (isInitialized) return

    fun Config.writeToFile(file: File){
        fun Map<String,Any?>.toText(): String {
            return GsonBuilder()
                .setPrettyPrinting()
                .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                .create()
                .toJson(this)!!
        }
        fun String.toFile(file: File){
            if (!file.exists()){
                file.createNewFile()
            }

            file.writeText(this)
        }

        this
        .toMap()
        .toText()
        .toFile(file)
    }
    fun Config.overwriteFromFile(file: File) {
        fun String.toMap(): Map<String, Any?> {
            @Suppress("UNCHECKED_CAST")
            return Gson().fromJson(this, Map::class.java) as Map<String, Any?>
        }

        file
        .readText()
        .toMap()
        .let {
            this.override(
                it,
                UnaverageTweaks::logInvalidConfigKey,
                UnaverageTweaks::logNonExistentConfigKey
            )
        }
    }

    val file = FabricLoader.getInstance().configDir.resolve(FILE_NAME).toFile()

    if (!file.exists()){
        GlobalConfig.writeToFile(file)
    }
    else {
        GlobalConfig.overwriteFromFile(file)
        GlobalConfig.writeToFile(file)
    }

    isInitialized = true
}

var isInitialized = false


fun isEnabled(mixinName: String): Boolean{
    val mixinName = mixinName.split('.').let{ it[it.lastIndex-1] }

    val map = mapOf(
        "allays_can_plant_crops" to GlobalConfig.allays_can_plant_crops ,
        "animals_custom_feeding" to GlobalConfig.animals_custom_feeding.enable ,
        "animals_heal_when_fed" to GlobalConfig.animals_heal_when_fed.enable ,
        "bane_of_arthropods_extra" to GlobalConfig.bane_of_arthropods_extra.enable,
        "creepers_avoid_cats_further" to GlobalConfig.creepers_avoid_cats_further.enable,
        "enchantments_blacklist" to GlobalConfig.enchantments_blacklist.enable,
        "enchantments_are_capped" to GlobalConfig.enchantments_are_capped.enable,
        "enchantments_transfer_to_book" to GlobalConfig.enchantments_transfer_to_book.enable,
        "fire_protection_offers_lava_immunity" to GlobalConfig.fire_protection_offers_lava_immunity.enable,
        "fire_protection_offers_meele_protection" to GlobalConfig.fire_protection_offers_melee_protection.enable,
        "frost_walker_melts_at_night" to GlobalConfig.frost_walker_melts_at_night,
        "glow_squids_better_spawn" to GlobalConfig.glow_squids_better_spawn,
        "horses_harder_to_tame" to GlobalConfig.horses_harder_to_tame,
        "piglins_and_hoglins_fire_immune" to GlobalConfig.piglins_and_hoglins_are_fire_immune,
        "ridden_pigs_are_faster" to GlobalConfig.pigs_ridden_are_faster.enable,
        "shields_no_longer_prevent_knockback" to GlobalConfig.shields_no_longer_prevent_knockback,
        "thorns_no_longer_wears_down_armor" to GlobalConfig.thorns_no_longer_wears_down_armor,
        "tools_custom_repair_rate" to GlobalConfig.tools_custom_repair_rate.enable,
        "tools_max_durability_decay" to GlobalConfig.tools_max_durability_decay.enable,
        "tools_repair_takes_no_xp" to GlobalConfig.tools_repair_takes_zero_xp,
        "village_less_fights" to GlobalConfig.village_less_fight,
        "xp" to (GlobalConfig.xp.disable_xp || GlobalConfig.xp.disable_bar)
    )

    return map[mixinName] ?: throw RuntimeException("$mixinName not registered here")
}

@Suppress("ClassName")
object GlobalConfig: Config {
    //Config name noun should be placed first

    @JvmField
    var allays_can_plant_crops = true

    object animals_custom_feeding: Config {
        @JvmField
        var enable = true

        var affects = mapOf(
            "minecraft:pig" to setOf(
                "minecraft:apple",
                "minecraft:beetroot",
                "minecraft:carrot",
                "minecraft:wheat",
            )
        )
    }
    
    object animals_heal_when_fed: Config {
        @JvmField
        var enable = true

        var affected = setOf(
            "minecraft:chicken",
            "minecraft:cow",
            "minecraft:mooshroom",
            "minecraft:parrot",
            "minecraft:pig",
        )
    }

    object bane_of_arthropods_extra: Config {

        @JvmField
        var enable = true

        var extra_mobs_affected = setOf(
            "minecraft:guardian",
            "minecraft:elder_guardian"
        )

    }

    object creepers_avoid_cats_further: Config{

        @JvmField
        var enable = true

        @JvmStatic
        var distance = 16
            set(value) {
                if (value <= 0) throw InvalidValueException()

                field = value
            }
    }

    object enchantments_are_capped: Config{
        @JvmField
        var enable = true

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
            "minecraft:iron_.+" to 3.0,
            "minecraft:netherite_.+" to 1.5,
            "minecraft:shears" to 2.0,
        )

        @JvmStatic
        var tool_tip_decimal_places = 1
            set(value) {
                if (value < 0) throw InvalidValueException()

                field = value
            }
    }

    object enchantments_transfer_to_book: Config{
        @JvmField
        var enable = true

        @JvmStatic
        var transfer_percentage = 0.75
            set(value) {
                if (value<0) throw InvalidValueException()
                if (value>1) throw InvalidValueException()

                field = value
            }
    }

    object enchantments_blacklist: Config {
        @JvmField
        var enable = true

        @JvmField
        var blacklisted = setOf(
            "minecraft:protection"
        )
    }

    object fire_protection_offers_lava_immunity: Config {
        @JvmField
        var enable = true

        @JvmField
        var seconds_of_lava_immunity_per_levels = 1.0
    }

    object fire_protection_offers_melee_protection: Config{
        @JvmField
        var enable = true

        @JvmField
        var protects_from = setOf("minecraft:blazed", "minecraft:magma_cube")
    }

    @JvmField
    var frost_walker_melts_at_night = true

    @JvmField
    var glow_squids_better_spawn = true

    @JvmField
    var horses_harder_to_tame = true

    @JvmField
    var piglins_and_hoglins_are_fire_immune = true

    object pigs_ridden_are_faster: Config {
        @JvmField
        var enable = true

        @JvmStatic
        var speed_multiplier = 2.0
            set(value) {
                if (value < 1) throw InvalidValueException()

                field = value
            }
    }

    @JvmField
    var shields_no_longer_prevent_knockback = true

    @JvmField
    var thorns_no_longer_wears_down_armor = true
    
    object tools_custom_repair_rate: Config{
        @JvmField
        var enable = true
        
        var ingots_to_fully_repair = mapOf(
            "minecraft:.+_helmet" to 3,
            "minecraft:.+_chestplate" to 5,
            "minecraft:.+_leggings" to 4,
            "minecraft:.+_boots" to 2,

            "minecraft:.+_axe" to 2,
            "minecraft:.+_hoe" to 1,
            "minecraft:.+_pickaxe" to 2,
            "minecraft:.+_shovel" to 1,
            "minecraft:.+_sword" to 1,
        )
    }

    object tools_max_durability_decay: Config{
        @JvmField
        var enable = false

        @JvmStatic
        var decay_rate = 1000
            set(value) {
                if (value <= 0) throw InvalidValueException()

                field = value
            }
    }

    @JvmField
    var tools_repair_takes_zero_xp = false

    object xp: Config{
        @JvmField
        var disable_xp = false

        @JvmField
        var disable_bar = false
    }

    @JvmField
    var village_less_fight = true
}

