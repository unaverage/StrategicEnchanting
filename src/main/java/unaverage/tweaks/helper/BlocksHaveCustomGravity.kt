package unaverage.tweaks.helper

import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.registry.Registries
import unaverage.tweaks.GlobalConfig

val Block.hasCustomGravity: Boolean
    get() {
        return GlobalConfig.blocks_have_custom_gravity
            .blocks_affected
            .containsRegex(
                this.getID(Registries.BLOCK)
            )
    }
