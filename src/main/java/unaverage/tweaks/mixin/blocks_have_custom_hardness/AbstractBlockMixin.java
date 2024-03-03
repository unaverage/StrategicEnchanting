package unaverage.tweaks.mixin.blocks_have_custom_hardness;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static unaverage.tweaks.helper.BlocksHaveCustomHardnessKt.*;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockMixin {
    @Shadow public abstract Block getBlock();

    @Inject(
        method = "getHardness",
        at = @At("HEAD"),
        cancellable = true
    )
    void useCustomHardness(CallbackInfoReturnable<Float> cir){
        var result = getCustomHardness(this.getBlock());
        if (result == null) return;

        cir.setReturnValue((float)(double)result);
    }
}
