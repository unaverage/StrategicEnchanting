package unaverage.tweaks.mixin.bridging_is_disabled.scaffolding_bridging_is_disabled;

import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.item.ScaffoldingItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemPlacementContext.class)
public class ItemContextMixin {
    @Inject(
        method = "offset",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void disabledScaffoldingPillaring(ItemPlacementContext context, BlockPos pos, Direction side, CallbackInfoReturnable<ItemPlacementContext> cir){
        if (!context.getStack().isOf(Items.SCAFFOLDING)) return;
        if (side == Direction.UP || side == Direction.DOWN) return;

        cir.setReturnValue(null);
    }
}
