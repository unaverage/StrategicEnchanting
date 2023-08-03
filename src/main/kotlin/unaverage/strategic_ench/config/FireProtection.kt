package unaverage.strategic_ench.config

import com.google.common.collect.ImmutableSet
import net.minecraft.entity.EntityType
import unaverage.strategic_ench.config.FireProtection.AddMobProtection
import unaverage.strategic_ench.config.FireProtection.SecondsOfLavaImmunityPerLevel

/**
 * Contains all the configuration values relating to the fire protection enchantment
 */
class FireProtection internal constructor() {
    var secondsOfLavaImmunityPerLevel: SecondsOfLavaImmunityPerLevel
    var addMobProtection: AddMobProtection

    init {
        secondsOfLavaImmunityPerLevel = SecondsOfLavaImmunityPerLevel()
        addMobProtection = AddMobProtection()
    }

    /**
     * @param t The entity type of the mob being tested
     * {@return true if fire protection is configured to protect against this entity's melee attacks}
     */
    fun protectsAgainst(t: EntityType<*>?): Boolean {
        return false
    }

    /**
     * {@return true if temporary lava immunity is enabled}
     */
    fun hasLavaImmunity(): Boolean {
        return secondsOfLavaImmunityPerLevel.value > 0
    }

    /**
     * {@return the duration in ticks of the temporary laval immunity}
     */
    fun getLavaImmunityDuration(level: Int): Int {
        return Math.round(secondsOfLavaImmunityPerLevel.value * 20).toInt() * level
    }

    class SecondsOfLavaImmunityPerLevel {
        var value: Double

        init {
            value = DEFAULT
        }

        companion object {
            const val DEFAULT = 1.0
        }
    }

    class AddMobProtection {
        var value: Set<String>

        init {
            value = HashSet()
        }

        companion object {
            fun getDefault(newFile: Boolean): Set<String> {
                return if (!newFile) java.util.Set.of() else ImmutableSet.of(
                    "minecraft:blaze",
                    "minecraft:magma_cube"
                )
            }
        }
    }

    companion object {
        @JvmField
        var COOL_DOWN_SEC = 30
        fun getDefault(newFile: Boolean): Map<String, Any> {
            return java.util.Map.of(
                SecondsOfLavaImmunityPerLevel::class.java.simpleName, SecondsOfLavaImmunityPerLevel.DEFAULT,
                AddMobProtection::class.java.simpleName, AddMobProtection.getDefault(newFile)
            )
        }
    }
}