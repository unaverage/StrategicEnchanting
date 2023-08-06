package unaverage.strategic_ench.config

import com.google.common.math.DoubleMath.roundToInt
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.EntityType
import net.minecraft.registry.Registries
import unaverage.strategic_ench.StrategicEnchanting
import unaverage.strategic_ench.cachedContain
import unaverage.strategic_ench.cachedGetID
import java.io.File
import kotlin.math.roundToInt
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

const val FILE_NAME = StrategicEnchanting.MOD_ID + ".json"

@JvmField
var configInitialized: Boolean = false

fun affectedByBaneOfAnthropod(e: EntityType<*>): Boolean {
    return GlobalConfig
        .BaneOfArthropod
        .Affects
        .toSet()
        .contains(
            e.cachedGetID(Registries.ENTITY_TYPE)
        )
}

fun enchantmentIsBlacklisted(e: Enchantment): Boolean {
    return GlobalConfig
        .Enchantments
        .Blacklisted
        .toSet()
        .cachedContain(
            e.cachedGetID(Registries.ENCHANTMENT),
        )
}

fun fireProtectionHasLavaDuration(): Boolean{
    return GlobalConfig.FireProtection.SecondsOfLavaImmunityPerLevel != 0.0
}

fun getFireProtectionLavaImmunityDuration(level: Int): Int {
    return GlobalConfig
        .FireProtection
        .SecondsOfLavaImmunityPerLevel
        .let { it * level }
        .let{ it * 20 }
        .roundToInt()
}

fun fireProtectionProtectsAgainst(e: EntityType<*>): Boolean {
    return GlobalConfig
        .FireProtection
        .ProtectsAgainst
        .toSet()
        .cachedContain(
            e.cachedGetID(Registries.ENTITY_TYPE),
        )
}

fun thornsWearDownArmor(): Boolean{
    return !GlobalConfig.Thorns.NoLongerWearsDownArmor;
}

fun runGlobalConfig() {
    fun Config.toMap(): Map<String, Any?>{
        val result = mutableMapOf<String,Any?>()

        this::class.nestedClasses.forEach{
            it as KClass<out Config>

            result += it.simpleName.toString() to it.objectInstance!!.toMap()
        }

        this::class.memberProperties.forEach {
            result += it.name to it.call(this)!!
        }

        return result
    }
    fun Map<String,Any?>.toText(): String {
        return GsonBuilder()
            .setPrettyPrinting()
            .create()
            .toJson(this)!!
    }
    fun String.toFile(file: File){
        if (!file.exists()){
            file.createNewFile()
        }

        file.writeText(this)
    }

    fun File.toText(): String {
        return readText()
    }
    fun String.toMap(): Map<String,Any?>{
        return Gson().fromJson(this, Map::class.java) as Map<String,Any?>
    }
    fun Map<String, Any?>.writeToObj(obj: Config){
        forEach { (k, v) ->
            obj::class
                .nestedClasses
                .firstOrNull { it.simpleName == k }
                ?.let {
                    (v as Map<String, Any?>).writeToObj(it.objectInstance!! as Config)
                    return@forEach
                }

            obj::class
                .memberProperties
                .firstOrNull { it.name == k }
                ?.let {
                    try {
                        it as KMutableProperty<*>

                        it.setter.call(obj, v)
                    }
                    catch (e: Exception){
                        throw RuntimeException("$k $v ${if (v==null) "null" else v::class}", e)
                    }

                    return@forEach
                }

            throw RuntimeException()
        }
    }

    val file = FabricLoader.getInstance().configDir.resolve(FILE_NAME).toFile()

    if (!file.exists()){
        GlobalConfig
        .toMap()
        .toText()
        .toFile(file)
    }
    else {
        file
        .toText()
        .toMap()
        .writeToObj(GlobalConfig)

        GlobalConfig
        .toMap()
        .toText()
        .toFile(file)
    }

    configInitialized = true
}

interface Config
object GlobalConfig: Config{
    object BaneOfArthropod: Config{
        var Affects = listOf("minecraft:guardian, minecraft:elder_guardian")
    }

    object Enchantments: Config{
        var Blacklisted = listOf("minecraft:protection")

        var Weights = mapOf(
            "1" to listOf(1.0),
            "2" to listOf(.5, 1.0),
            "3" to listOf(.25, .5, 1.0),
            "4" to listOf(.25, .5, .75, 1.0),
            "5" to listOf(.25, .25, .5, .75, 1.0),
            "modid:example" to listOf(0.25, .5, .75, 1.0),
        )

        var ItemCapacities = mapOf(
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

    object FireProtection: Config{
        var SecondsOfLavaImmunityPerLevel = 1.0
        var ProtectsAgainst = listOf("minecraft:blazed", "minecraft:magma_cube")
    }

    object Thorns: Config{
        var NoLongerWearsDownArmor = true
    }

    object Zombie: Config{
        var LessKnockBack = true

        var onlyAttacksIronGoblems = true;
    }

    object Pig: Config{
        var ExtraFood = listOf(
            "minecraft:baked_potato",
            "minecraft:bread",
            "minecraft:carrot",
            "minecraft:apple",
            "minecraft:potato",
            "minecraft:wheat",
            "minecraft:egg",
        )

        var SaddledSpeedMultiplier = 5.0
    }

    object Animals: Config{
        var healWhenAte = true
    }

    object Shields: Config{
        var NoLongerPreventsKnockBack = true
    }

    object FrostWalker: Config{
        var meltsAtNight = true
    }
}