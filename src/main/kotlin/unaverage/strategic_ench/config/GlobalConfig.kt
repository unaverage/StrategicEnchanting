package unaverage.strategic_ench.config

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import net.fabricmc.loader.api.FabricLoader
import unaverage.strategic_ench.StrategicEnchanting

/**
 * Contains all the configuration values of this mod
 */
class GlobalConfig internal constructor() {
    @JvmField
    val enchantmentCaps: EnchantmentCaps
    @JvmField
    val blacklistEnchantment: BlacklistEnchantment
    @JvmField
    val fireProtection: FireProtection
    @JvmField
    val baneOfArthropod: BaneOfArthropod
    @JvmField
    val thorn: Thorn

    init {
        enchantmentCaps = EnchantmentCaps()
        blacklistEnchantment = BlacklistEnchantment()
        fireProtection = FireProtection()
        baneOfArthropod = BaneOfArthropod()
        thorn = Thorn()
    }

    companion object {
        const val FILE_NAME = StrategicEnchanting.MOD_ID + ".json"
        /**
         * Is null if [GlobalConfig.run] was not called
         */
        @JvmField
        var INSTANCE: GlobalConfig? = null
        fun getDefaultConfig(newFile: Boolean): Map<String, Any> {
            return java.util.Map.of(
                EnchantmentCaps::class.java.simpleName, EnchantmentCaps.Companion.getDefault(newFile),
                BlacklistEnchantment::class.java.simpleName, BlacklistEnchantment.Companion.getDefault(newFile),
                FireProtection::class.java.simpleName, FireProtection.Companion.getDefault(newFile),
                BaneOfArthropod::class.java.simpleName, BaneOfArthropod.Companion.getDefault(newFile),
                Thorn::class.java.simpleName, Thorn.Companion.DEFAULT
            )
        }

        /**
         * Runs the configuration, setting all of the
         */
        @JvmStatic
        fun run() {
            val path = FabricLoader.getInstance().configDir.resolve(FILE_NAME)

            INSTANCE = GlobalConfig()
        }
    }
}