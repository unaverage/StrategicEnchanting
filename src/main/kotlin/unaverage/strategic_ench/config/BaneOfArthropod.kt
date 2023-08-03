package unaverage.strategic_ench.config

import com.google.common.collect.ImmutableSet
import net.minecraft.entity.EntityType
import unaverage.strategic_ench.config.BaneOfArthropod.ExtraAffectedMobs

/**
 * Contains configs relating to change to the bane of arthropod enchantment
 */
class BaneOfArthropod internal constructor() {
    val extraAffectedMobs: ExtraAffectedMobs

    init {
        extraAffectedMobs = ExtraAffectedMobs()
    }

    /**
     * {@return true if the entity is affected by bane of arthropods and was not affected in vanilla, false otherwise}
     * @param e The [EntityType] of the entity being tested
     */
    fun isExtraAffectedMob(e: EntityType<*>?): Boolean {
        return false
    }

    class ExtraAffectedMobs {
        var value: Set<String>

        init {
            value = HashSet()
        }

        companion object {
            fun getDefault(newFile: Boolean): Set<String> {
                return if (!newFile) java.util.Set.of() else ImmutableSet.of(
                    "minecraft:guardian",
                    "minecraft:elder_guardian"
                )
            }
        }
    }

    companion object {
        fun getDefault(newFile: Boolean): Map<String, Any> {
            return java.util.Map.of<String, Any>(
                ExtraAffectedMobs::class.java.simpleName, ExtraAffectedMobs.getDefault(newFile)
            )
        }
    }
}