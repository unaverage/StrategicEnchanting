package unaverage.tweaks.mixin.bridging_is_disabled;

import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static unaverage.tweaks.helper.BridgingIsDisabledKt.*;
import static unaverage.tweaks.helper.PillaringIsDisabledKt.*;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(
        method = "useOnBlock",
        at = @At(
            value = "HEAD"
        ),
        cancellable = true
    )
    void cancelBridging(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir){
        var placedPos = new ItemPlacementContext(context).getBlockPos();

        //checks if item is exempt
        {
            //shouldn't count if the item is explicitly exempt in the config
            var item = ((ItemStack)(Object)this).getItem();
            if (isExempt(item)) return;
        }

        //checks if player is exempt
        {
            //shouldn't count if the player is null or in creative mode
            if (context.getPlayer() == null) return;
            if (context.getPlayer().isCreative()) return;
        }

        //checks if it counts as bridging by player location
        {
            var pointedBlock = context.getBlockPos();

            var playerStandingPos = getLastSupportingBlock(context.getPlayer(), context.getWorld());

            //if the player's distance to the pointed block is greater than the player's distance to the next placed pos,
            //then the player is placing across the room and should not be counted as bridging
            if (playerStandingPos.getManhattanDistance(pointedBlock) > playerStandingPos.getManhattanDistance(placedPos)) return;

            //shouldn't count if the player is placing a block above themselves
            if (playerStandingPos.getY() < placedPos.getY() - 1) return;
        }

        //checks if it counts as bridging from block surroundings
        {
            var world = context.getWorld();

            //shouldn't be counted if there's a solid block underneath
            if (!world.getBlockState(placedPos.down()).isAir()) return;

            //shouldn't be counted if there are more than 1 non-air orthogonal block surrounding it
            int count = 0;
            for (var xi: new int[]{-1, 1}){
                if (!world.getBlockState(placedPos.add(xi, 0, 0)).isAir()) count += 1;
            }
            for (var zi: new int[]{-1, 1}){
                if (!world.getBlockState(placedPos.add(0, 0, zi)).isAir()) count += 1;
            }
            if (count > 1) return;
        }

        cir.setReturnValue(
            ActionResult.SUCCESS
        );
    }
}
