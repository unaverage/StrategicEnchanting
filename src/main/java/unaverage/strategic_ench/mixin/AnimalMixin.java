package unaverage.strategic_ench.mixin;

import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import unaverage.strategic_ench.config.GlobalConfig;

import static unaverage.strategic_ench.config.GlobalConfigKt.configInitialized;

@Mixin(AnimalEntity.class)
public class AnimalMixin {
    @Inject(
        method = "interactMob",
        at = @At("RETURN"),
        cancellable = true
    )
    private void injectHealIfHurt(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir){
        if (!configInitialized) return;

        if (!GlobalConfig.Animals.INSTANCE.getHealWhenAte()) return;

        var self = (AnimalEntity)(Object)this;

        if (self instanceof WolfEntity) return;
        if (self instanceof HorseEntity) return;

        if (!self.isBreedingItem(player.getStackInHand(hand))) return;

        if (self.getHealth() < self.getMaxHealth()) {
            self.heal(1);
            cir.setReturnValue(
                ActionResult.SUCCESS
            );
        }
    }

    @Inject(
        method = "canEat",
        at = @At("RETURN"),
        cancellable = true
    )
    private void preventLoveModeWhenHurt(CallbackInfoReturnable<Boolean> cir){
        if (!configInitialized) return;

        if (!GlobalConfig.Animals.INSTANCE.getHealWhenAte()) return;

        var self = (AnimalEntity)(Object)this;

        if (self instanceof WolfEntity) return;
        if (self instanceof HorseEntity) return;

        if (self.getHealth() < self.getMaxHealth()) {
            cir.setReturnValue(false);
        }
    }
}
