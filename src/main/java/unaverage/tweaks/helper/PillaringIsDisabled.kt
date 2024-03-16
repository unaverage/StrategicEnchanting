package unaverage.tweaks.helper

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.util.registry.Registry
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.WorldView
import unaverage.tweaks.GlobalConfig

val Item.canPillarWith: Boolean
    get(){
        return GlobalConfig.pillaring_is_disabled.exempt_blocks
            .containsRegex(
                this.getID(Registry.ITEM)
            )
    }

fun PlayerEntity.getLastSupportingBlock(world: WorldView): BlockPos {
    fun isSolid(p: BlockPos): Boolean {
        return world.getBlockState(p).isSolidSurface(world, p, this, Direction.DOWN)
    }

    this.blockPos.takeIf(::isSolid)?.let { return it }

    for (y in listOf(-1, -2)){

        //checks orthogonal first
        for (x in listOf(-1, 1)) {
            this.blockPos.add(x,y,0).takeIf(::isSolid)?.let { return it }
        }
        for (z in listOf(-1, 1)) {
            this.blockPos.add(0,y,z).takeIf(::isSolid)?.let { return it }
        }

        //checks corners last
        for (x in listOf(-1, 1)){
            for (z in listOf(-1, 1)) {
                this.blockPos.add(x,y,z).takeIf(::isSolid)?.let { return it }
            }
        }
    }
    return this.blockPos
}