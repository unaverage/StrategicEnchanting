package unaverage.tweaks.mixin.blocks_have_custom_blast_resistance;

import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import unaverage.tweaks.HelperKt;

@Mixin(Block.class)
public class BlocksMixin {
    @Inject(
        method = "getBlastResistance",
        at = @At("HEAD"),
        cancellable = true
    )
    void useCustomBlastResistance(CallbackInfoReturnable<Float> cir){
        var result = HelperKt.getCustomBlastResistance((Block)(Object)this);
        if (result == null) return;

        cir.setReturnValue((float)(double)result);
    }
}
