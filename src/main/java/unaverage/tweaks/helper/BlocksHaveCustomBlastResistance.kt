package unaverage.tweaks.helper

import net.minecraft.block.Block
import net.minecraft.util.registry.Registry
import unaverage.tweaks.GlobalConfig

val Block.customBlastResistance: Double?
    get(){
        val id = Registry.BLOCK.getId(this)

        return GlobalConfig.blocks_have_custom_blast_resistance.blocks_affected
            .getWithRegex(id.namespace + ":" + id.path)
    }