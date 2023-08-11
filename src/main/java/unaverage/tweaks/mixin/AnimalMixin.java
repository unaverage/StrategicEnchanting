package unaverage.tweaks.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import unaverage.tweaks.GlobalConfig;

@Mixin(AnimalEntity.class)
public abstract class AnimalMixin extends PassiveEntity {
    @Shadow public abstract boolean isBreedingItem(ItemStack stack);

    @Shadow protected abstract void eat(PlayerEntity player, Hand hand, ItemStack stack);

    protected AnimalMixin(EntityType<? extends PassiveEntity> entityType, World world) {super(entityType, world);}

    @SuppressWarnings("ConstantConditions")
    @Inject(
        method = "interactMob",
        at = @At("RETURN"),
        cancellable = true
    )
    private void injectHealIfHurt(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir){
        if (!GlobalConfig.Miscellaneous.animals_heal_when_eat) return;

        if ((Object)this instanceof WolfEntity) return;
        if ((Object)this instanceof HorseEntity) return;

        if (!this.isBreedingItem(player.getStackInHand(hand))) return;

        if (this.getHealth() < this.getMaxHealth()) {
            this.heal(1);

            this.eat(player, hand, player.getStackInHand(hand));

            cir.setReturnValue(
                ActionResult.SUCCESS
            );
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(
        method = "canEat",
        at = @At("RETURN"),
        cancellable = true
    )
    private void preventLoveModeWhenHurt(CallbackInfoReturnable<Boolean> cir){
        if (!GlobalConfig.Miscellaneous.animals_heal_when_eat) return;

        if ((Object)this instanceof WolfEntity) return;
        if ((Object)this instanceof HorseEntity) return;

        if (this.getHealth() < this.getMaxHealth()) {
            cir.setReturnValue(false);
        }
    }
}
