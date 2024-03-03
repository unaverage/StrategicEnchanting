package unaverage.tweaks.mixin.pillaring_is_disabled;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
    void cancelPillaring(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir){
        var item = ((ItemStack)(Object)this).getItem();

        if (getCanPillarWith(item)) return;

        if (context.getPlayer() == null) return;
        if (context.getPlayer().isCreative()) return;

        var playerPos = context.getPlayer().getBlockPos();
        var placedPos = context.getBlockPos();

        if (context.getPlayer().isOnGround()) return;

        if (playerPos.getY() < placedPos.getY()) return;

        cir.setReturnValue(
            ActionResult.SUCCESS
        );
    }
}
