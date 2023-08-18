package unaverage.tweaks.mixin.ridden_pigs_are_faster;

import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
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
    private void makeRiddenPigsFaster(PlayerEntity controllingPlayer, CallbackInfoReturnable<Float> cir){
        if (!GlobalConfig.pigs_ridden_are_faster.enable) return;

        var multiplier = GlobalConfig.pigs_ridden_are_faster.getSpeed_multiplier();

        cir.setReturnValue(
            (float)(cir.getReturnValue() * multiplier)
        );
    }
}
