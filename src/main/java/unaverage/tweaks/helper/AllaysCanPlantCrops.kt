package unaverage.tweaks.helper

import net.fabricmc.fabric.api.registry.VillagerPlantableRegistry
import net.minecraft.block.BlockState
import net.minecraft.block.CropBlock
import net.minecraft.entity.passive.AllayEntity
import net.minecraft.item.BlockItem
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldView

fun getNearestFarmPos(pos: BlockPos, cropBlock: BlockState, world: WorldView): BlockPos?{
    iterateCube(5) { i, j, k ->
        @Suppress("NAME_SHADOWING")
        val pos = BlockPos(
            pos.x + i,
            pos.y + j,
            pos.z + k
        )

        if (cropBlock.canPlaceAt(world, pos) && world.getBlockState(pos).isAir) {
            return pos
        }
    }
    return null
}

val AllayEntity.heldItemAsCropBlock: BlockState?
    get() {
        val item = inventory.getStack(0)
        if (item == null) return null
        if (item.count < 0) return null

        if (!VillagerPlantableRegistry.getItems().contains(item.item)) return null
        if (item.item !is BlockItem) return null

        val crop = (item.item as BlockItem).block as? CropBlock
        if (crop == null) return null

        return crop.withAge(0)
    }

