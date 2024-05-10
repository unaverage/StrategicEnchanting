package unaverage.tweaks.mixin.glowstone_dust_can_make_signs_glow;

import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSignBlock.class)
public class SignMixin {

    @Inject(
        method = "onUse",
        at = @At(
            value = "HEAD"
        ),
        cancellable = true
    )
    void glowstone_can_make_signs_glow(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir){
        var itemStack = player.getStackInHand(player.getActiveHand());

        if (!itemStack.isOf(Items.GLOWSTONE_DUST)) return;

        if (!(world.getBlockEntity(pos) instanceof SignBlockEntity signBlockEntity)) return;

        if (world.isClient) {
            cir.setReturnValue(
                signBlockEntity.isWaxed() ? ActionResult.SUCCESS : ActionResult.CONSUME
            );
        }
        else {
            signBlockEntity.changeText(
                text -> text.withGlowing(true),
                signBlockEntity.isPlayerFacingFront(player)
            );

            world.emitGameEvent(GameEvent.BLOCK_CHANGE, signBlockEntity.getPos(), GameEvent.Emitter.of(player, signBlockEntity.getCachedState()));
            player.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));

            if (!player.isCreative()) itemStack.decrement(1);

            cir.setReturnValue(
                ActionResult.SUCCESS
            );
        }
    }
}
