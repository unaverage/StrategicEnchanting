package unaverage.tweaks.mixin.pigs_ridden_are_faster;

import net.minecraft.entity.passive.PigEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import unaverage.tweaks.GlobalConfig;

@Mixin(PigEntity.class)
public class PigMixin {
    @Inject(
        method = "getSaddledSpeed",
        at = @At("RETURN"),
        cancellable = true
    )
    private void makeRiddenPigsFaster(CallbackInfoReturnable<Float> cir){
        var multiplier = GlobalConfig.pigs_ridden_are_faster.getSpeed_multiplier();

        cir.setReturnValue(
            (float)(cir.getReturnValue() * multiplier)
        );
    }
}
