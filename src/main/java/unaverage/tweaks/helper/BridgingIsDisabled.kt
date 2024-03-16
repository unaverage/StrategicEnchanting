package unaverage.tweaks.helper

import net.minecraft.item.Item
import net.minecraft.util.registry.Registry
import unaverage.tweaks.GlobalConfig

val Item.isExempt: Boolean
    get(){
        return GlobalConfig.bridging_is_disabled.exempt_blocks
            .containsRegex(
                this.getID(Registry.ITEM)
            )
    }