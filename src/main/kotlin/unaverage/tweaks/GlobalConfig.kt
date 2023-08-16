package unaverage.tweaks

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
import net.fabricmc.loader.api.FabricLoader
import roland_a.simple_configs.Config
import roland_a.simple_configs.Config.Companion.override
import roland_a.simple_configs.Config.Companion.toMap
import java.io.File

const val FILE_NAME = UnaverageTweaks.MOD_ID + ".json"

fun runGlobalConfig() {
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
}

object GlobalConfig: Config {
    object EnchantmentCaps: Config{
        var enchantment_weights = mapOf(
            "1" to listOf(1.0),
            "2" to listOf(.5, 1.0),
            "3" to listOf(.25, .5, 1.0),
            "4" to listOf(.25, .5, .75, 1.0),
            "5" to listOf(.25, .25, .5, .75, 1.0),
            "modid:example" to listOf(0.25, .5, .75, 1.0),
        )
        set(value) {
            field = value
                .mapValues {
                    (_, it)-> it.map { (it as Number).toDouble() }
                }
        }

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
        set(value) {
            field = value.mapValues { (_, it)->(it as Number?)?.toDouble() }
        }

        @JvmField
        var tool_tip_decimal_places = 1
    }

    object XP: Config{
        @JvmField
        var anvil_no_longer_requires_xp = true

        @JvmField
        var disable_xp = true

        @JvmField
        var tool_decay_rate = 1000
    }

    //Config name noun should be placed first

    @JvmField
    var allays_can_plant_crops = true

    @JvmField
    var animals_heal_when_fed = setOf(
        "minecraft:chicken",
        "minecraft:cow",
        "minecraft:mooshroom",
        "minecraft:parrot",
        "minecraft:pig",
    )

    @JvmField
    var animals_eat = mapOf(
        "minecraft:pig" to setOf(
            "minecraft:apple",
            "minecraft:beetroot",
            "minecraft:carrot",
            "minecraft:wheat",
        )
    )

    @JvmField
    var bane_of_arthropods_also_affects = setOf("minecraft:guardian, minecraft:elder_guardian")

    @JvmField
    var creepers_avoid_cats_at = 16

    @JvmField
    var enchantment_blacklist = setOf("minecraft:protection")

    @JvmField
    var fire_protection_lava_immunity = 1.0

    @JvmField
    var fire_protection_protects_against = setOf("minecraft:blazed", "minecraft:magma_cube")

    @JvmField
    var frost_walker_melts_at_night = true

    @JvmField
    var glow_squids_better_spawn = true

    @JvmField
    var piglins_and_hoglins_are_fire_immune = true

    @JvmField
    var pigs_ridden_speed_boost = 2.0

    @JvmField
    var shields_no_longer_prevent_knockback = true

    @JvmField
    var thorns_no_longer_wears_down_armor = true

    var tools_ingots_to_fully_repair = mapOf(
        "minecraft:.+_helmet" to 3,
        "minecraft:.+_chestplate" to 5,
        "minecraft:.+_leggings" to 4,
        "minecraft:.+_boots" to 2,

        "minecraft:.+_axe" to 2,
        "minecraft:.+_shovel" to 1,
        "minecraft:.+_pickaxe" to 2,
        "minecraft:.+_hoe" to 1,
    )
    set(value) {
        field = value.mapValues { (_, it)->(it as Number).toInt() }
    }

    @JvmField
    var village_less_fight = true
}

