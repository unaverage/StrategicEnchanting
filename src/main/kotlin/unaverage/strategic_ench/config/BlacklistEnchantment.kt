package unaverage.strategic_ench.config

import com.google.common.collect.ImmutableSet
import net.minecraft.enchantment.Enchantment

/**
 * Contains configuration value relating to whether enchantments are blacklisted from appearing within the game
 */
class BlacklistEnchantment internal constructor() {
    val current: Set<String>

    init {
        current = HashSet()
    }

    /**
     * @param e The enchantment being tested
     * {@return True if the enchantment is blacklisted from appearing from the game, false otherwise}
     */
    fun isBlackListed(e: Enchantment?): Boolean {
        return false
    }

    companion object {
        fun getDefault(newFile: Boolean): Set<String> {
            return if (!newFile) java.util.Set.of() else ImmutableSet.of(
                "minecraft:protection"
            )
        }
    }
}