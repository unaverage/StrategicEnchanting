package unaverage.strategic_ench.config

import unaverage.strategic_ench.config.Thorn.StopArmorDamage
import java.util.Map

/**
 * Contains configuration values relating to the thorns enchantment
 */
class Thorn internal constructor() {
    val stopArmorDamage: StopArmorDamage

    init {
        stopArmorDamage = StopArmorDamage()
    }

    /**
     * {@return true if the thorns protection should not damage armor after inflicting damage, false otherwise}
     */
    fun stopArmorDamage(): Boolean {
        return stopArmorDamage.value
    }

    class StopArmorDamage {
        var value: Boolean

        init {
            value = DEFAULT
        }

        companion object {
            const val DEFAULT = true
        }
    }

    companion object {
        val DEFAULT = Map.of<String, Any>(
            StopArmorDamage::class.java.simpleName, StopArmorDamage.DEFAULT
        )
    }
}