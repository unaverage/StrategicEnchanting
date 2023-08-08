package unaverage.strategic_ench.mixin;

import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import unaverage.strategic_ench.config.GlobalConfig;

@Mixin(PigEntity.class)
public class PigMixin {
    @Inject(
        method = "getSaddledSpeed",
        at = @At("RETURN"),
        cancellable = true
    )
    private void injectPigNewSpeed(PlayerEntity controllingPlayer, CallbackInfoReturnable<Float> cir){
        double multiplier = GlobalConfig.Miscellaneous.ridden_pigs_speed_multiplied_by;

        if (multiplier <= 0 || multiplier == 1) return;

        cir.setReturnValue(
            (float)(cir.getReturnValue() * multiplier)
        );
    }
}
