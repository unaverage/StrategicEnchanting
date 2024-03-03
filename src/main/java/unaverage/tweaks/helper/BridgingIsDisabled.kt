package unaverage.tweaks.helper

import net.minecraft.item.Item
import net.minecraft.registry.Registries
import unaverage.tweaks.GlobalConfig

val Item.isExempt: Boolean
    get(){
        return GlobalConfig.bridging_is_disabled.exempt_blocks
            .containsRegex(
                this.getID(Registries.ITEM)
            )
    }