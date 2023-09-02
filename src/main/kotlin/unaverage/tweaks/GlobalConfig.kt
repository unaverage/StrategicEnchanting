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

private val packageToConfig = mutableMapOf<String, ()->Boolean>()

fun isEnabled(mixinName: String): Boolean{
    val mixinName = mixinName.split('.').let{ it[it.lastIndex-1] }

    return packageToConfig[mixinName]?.invoke() ?: throw RuntimeException("\"${mixinName}\" not registered here")
}

@Suppress("ClassName")
object GlobalConfig: Config {
    //Config name noun should be placed first

    var allays_can_plant_crops = true
    init {
        packageToConfig["allays_can_plant_crops"] = { allays_can_plant_crops}
    }

    object animals_have_custom_feeding: Config {
        var enable = false

        var affects = mapOf(
            "minecraft:pig" to setOf(
                "minecraft:apple",
                "minecraft:beetroot",
                "minecraft:carrot",
                "minecraft:wheat",
            )
        )
    }
    init {
        packageToConfig["animals_have_custom_feeding"] = { animals_have_custom_feeding.enable }
    }
    
    object animals_heal_when_fed: Config {
        var enable = false

        var affected = setOf(
            "minecraft:chicken",
            "minecraft:cow",
            "minecraft:mooshroom",
            "minecraft:parrot",
            "minecraft:pig",
        )
    }
    init {
        packageToConfig["animals_heal_when_fed"] = {animals_heal_when_fed.enable}
    }

    var anvils_take_zero_xp = false
    init {
        packageToConfig["anvils_take_zero_xp"] = {anvils_take_zero_xp}
    }

    object bane_of_arthropods_affects_more_mobs: Config {
        var enable = false

        var extra_mobs_affected = setOf(
            "minecraft:guardian",
            "minecraft:elder_guardian"
        )
    }
    init {
        packageToConfig["bane_of_arthropods_affects_more_mobs"] = {bane_of_arthropods_affects_more_mobs.enable}
    }

    object blocks_have_custom_hardness: Config{
        var enable = false

        var extra_blocks_affected = mapOf(
            "minecraft:grass_block" to 1.0,
            "minecraft:mud" to 1.0,
            "minecraft:mycelium" to 1.0,
            "minecraft:podzol" to 1.0,
            "minecraft:(.+_)?dirt(_.+)?" to 1.0,
            "minecraft:(.+_)?soil(_.+)?" to 1.0,
            "minecraft:(.+_)?sand(_.+)?" to 1.0,
        )
    }
    init {
        packageToConfig["blocks_have_custom_hardness"] = {blocks_have_custom_hardness.enable}
    }

    object blocks_have_custom_blast_resistance: Config{
        var enable = false

        var extra_blocks_affected = mapOf(
            "minecraft:grass_block" to 2.0,
            "minecraft:mud" to 2.0,
            "minecraft:mycelium" to 2.0,
            "minecraft:podzol" to 2.0,
            "minecraft:(.+_)?dirt(_.+)?" to 2.0,
            "minecraft:(.+_)?soil(_.+)?" to 2.0,
            "minecraft:(.+_)?sand(_.+)?" to 2.0,
        )
    }
    init {
        packageToConfig["blocks_have_custom_blast_resistance"] = {blocks_have_custom_blast_resistance.enable}
    }

    object creepers_avoid_cats_further: Config{
        var enable = false

        @JvmStatic
        var distance = 16
            set(value) {
                if (value <= 0) throw InvalidValueException()

                field = value
            }
    }
    init {
        packageToConfig["creepers_avoid_cats_further"] = {creepers_avoid_cats_further.enable}
    }

    object enchantments_can_transfer_to_books: Config{
        var enable = false

        @JvmStatic
        var transfer_percentage = 0.75
            set(value) {
                if (value<0) throw InvalidValueException()
                if (value>1) throw InvalidValueException()

                field = value
            }
    }
    init {
        packageToConfig["enchantments_can_transfer_to_books"] = {enchantments_can_transfer_to_books.enable}
    }

    object enchantments_can_be_blacklisted: Config {
        var enable = false

        var blacklisted = setOf(
            "minecraft:protection"
        )
    }
    init {
        packageToConfig["enchantments_can_be_blacklisted"] = {enchantments_can_be_blacklisted.enable}
    }

    var endermen_teleport_unreachable_players_closer = false
    init {
        packageToConfig["endermen_teleport_unreachable_players_closer"] = {endermen_teleport_unreachable_players_closer}
    }

    object fire_protection_offers_lava_immunity: Config {
        var enable = false

        var seconds_of_lava_immunity_per_levels = 1.0
    }
    init {
        packageToConfig["fire_protection_offers_lava_immunity"] = {fire_protection_offers_lava_immunity.enable}
    }

    object fire_protection_offers_melee_protection: Config{
        var enable = false

        var protects_from = setOf("minecraft:blazed", "minecraft:magma_cube")
    }
    init {
        packageToConfig["fire_protection_offers_melee_protection"] = {fire_protection_offers_melee_protection.enable}
    }

    var frost_walker_melts_at_night = true
    init {
        packageToConfig["frost_walker_melts_at_night"] = { frost_walker_melts_at_night}
    }

    var glow_squids_have_better_spawning = true
    init {
        packageToConfig["glow_squids_have_better_spawning"] = { glow_squids_have_better_spawning}
    }

    var horses_are_harder_to_tame = true
    init {
        packageToConfig["horses_are_harder_to_tame"] = { horses_are_harder_to_tame }
    }

    var piglins_and_hoglins_are_fire_immune = true
    init {
        packageToConfig["piglins_and_hoglins_are_fire_immune"] = { piglins_and_hoglins_are_fire_immune}
    }

    object pigs_ridden_are_faster: Config {
        var enable = false

        @JvmStatic
        var speed_multiplier = 2.0
            set(value) {
                if (value < 1) throw InvalidValueException()

                field = value
            }
    }
    init {
        packageToConfig["pigs_ridden_are_faster"] = {pigs_ridden_are_faster.enable}
    }

    var shields_no_longer_prevent_knockback = true
    init {
        packageToConfig["shields_no_longer_prevent_knockback"] = { shields_no_longer_prevent_knockback}
    }

    var thorns_no_longer_wear_down_armor = true
    init {
        packageToConfig["thorns_no_longer_wear_down_armor"] = { thorns_no_longer_wear_down_armor}
    }
    
    object tools_have_custom_repair_rate: Config{
        var enable = false
        
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
    init {
        packageToConfig["tools_have_custom_repair_rate"] = {tools_have_custom_repair_rate.enable}
    }

    object tools_have_limited_enchantment_capacity: Config{
        var enable = false

        var enchantment_weights_by_max_levels = mapOf(
            "1" to mapOf(
                "1" to 1.0
            ),
            "2" to mapOf(
                "1" to 0.5,
                "2" to 1.0
            ),
            "3" to mapOf(
                "1" to 0.25,
                "2" to 0.50,
                "3" to 1.00
            ),
            "4" to mapOf(
                "1" to 0.25,
                "2" to 0.50,
                "3" to 0.75,
                "4" to 1.00
            ),
            "5" to mapOf(
                "1" to 0.125,
                "2" to 0.25,
                "3" to 0.50,
                "4" to 0.75,
                "5" to 1.00,
            ),
        )

        var enchantment_weights_by_id = mapOf(
            "example_mod:example_enchantment" to mapOf(
                "1" to 0.25,
                "2" to 0.50,
                "3" to 0.75,
                "4" to 1.00
            ),
        )

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
    init {
        packageToConfig["tools_have_limited_enchantment_capacity"] = {tools_have_limited_enchantment_capacity.enable}
    }

    object tools_max_durability_will_decay: Config{
        var enable = false

        @JvmStatic
        var decay_rate = 500
            set(value) {
                if (value <= 0) throw InvalidValueException()

                field = value
            }
    }
    init {
        packageToConfig["tools_max_durability_will_decay"] = {tools_max_durability_will_decay.enable}
    }

    object xp_is_disabled: Config{
        var enable = false

        var allow_xp_bar = false
    }
    init {
        packageToConfig["xp_is_disabled"] = {xp_is_disabled.enable}
        packageToConfig["xp_bar_is_disabled"] = {xp_is_disabled.enable && !xp_is_disabled.allow_xp_bar}
    }

    var villages_has_less_fights = true
    init {
        packageToConfig["villages_have_less_fights"] = { villages_has_less_fights}
    }
}

