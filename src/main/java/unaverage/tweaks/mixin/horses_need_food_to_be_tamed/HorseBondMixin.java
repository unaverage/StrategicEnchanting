package unaverage.tweaks.mixin.horses_need_food_to_be_tamed;

import net.minecraft.entity.ai.goal.HorseBondWithPlayerGoal;
import net.minecraft.entity.passive.AbstractHorseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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
        return 0;
    }
}
