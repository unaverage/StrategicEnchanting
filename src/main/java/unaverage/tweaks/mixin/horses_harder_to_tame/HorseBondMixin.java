package unaverage.tweaks.mixin.horses_harder_to_tame;

import net.minecraft.entity.ai.goal.HorseBondWithPlayerGoal;
import net.minecraft.entity.passive.AbstractHorseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import unaverage.tweaks.GlobalConfig;

@Mixin(HorseBondWithPlayerGoal.class)
public class HorseBondMixin {
    @Redirect(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/passive/AbstractHorseEntity;addTemper(I)I"
        )
    )
    int cancelTemperWithoutFeeding(AbstractHorseEntity instance, int difference){
        if (!GlobalConfig.horses_harder_to_tame) return instance.addTemper(difference);

        return 0;
    }
}
