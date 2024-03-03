package unaverage.tweaks.helper

import net.minecraft.item.Item
import net.minecraft.registry.Registries
import unaverage.tweaks.GlobalConfig

val Item.ingotsToFullyRepair: Int?
    get() {
        return GlobalConfig.tools_have_custom_repair_rate.ingots_to_fully_repair
            .getWithRegex(
                this.getID(Registries.ITEM)
            )
    }