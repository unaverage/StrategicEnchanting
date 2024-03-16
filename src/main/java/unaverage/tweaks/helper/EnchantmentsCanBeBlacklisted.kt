package unaverage.tweaks.helper

import net.minecraft.enchantment.Enchantment
import net.minecraft.util.registry.Registry
import unaverage.tweaks.GlobalConfig

val Enchantment.isBlackListed: Boolean
    get() {
        return GlobalConfig.enchantments_can_be_blacklisted.blacklisted_enchantments
            .containsRegex(
                this.getID(Registry.ENCHANTMENT)
            )
    }