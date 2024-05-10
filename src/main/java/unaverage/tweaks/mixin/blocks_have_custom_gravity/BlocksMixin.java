package unaverage.tweaks.mixin.blocks_have_custom_gravity;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import unaverage.tweaks.helper.BlocksHaveCustomGravityKt;

import static net.minecraft.block.FallingBlock.canFallThrough;
import static unaverage.tweaks.helper.BlocksHaveCustomBlastResistanceKt.getCustomBlastResistance;
import static unaverage.tweaks.helper.BlocksHaveCustomGravityKt.*;

@Mixin(AbstractBlock.class)
public abstract class BlocksMixin {
    @Inject(
        method = "onBlockAdded",
        at = @At("HEAD")
    )
    void overrideOnBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci){
        if (!getHasCustomGravity(state.getBlock())) return;

        if (!canFallThrough(world.getBlockState(pos.down())) || pos.getY() < world.getBottomY()) return;

        FallingBlockEntity.spawnFromBlock(world, pos, state);
    }

    @Inject(
        method = "getStateForNeighborUpdate",
        at = @At("HEAD")
    )
    void overrideGetStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir){
        if (!getHasCustomGravity(state.getBlock())) return;

        if (!(world instanceof World)) return;

        if (!canFallThrough(world.getBlockState(pos.down())) || pos.getY() < world.getBottomY()) return;

        FallingBlockEntity.spawnFromBlock((World)world, pos, state);
    }

    @Inject(
        method = "scheduledTick",
        at = @At("HEAD")
    )
    void overrideScheduleTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci){
        if (!getHasCustomGravity(state.getBlock())) return;

        if (!canFallThrough(world.getBlockState(pos.down())) || pos.getY() < world.getBottomY()) return;

        FallingBlockEntity.spawnFromBlock(world, pos, state);
    }


}
