package unaverage.tweaks.mixin.animals_heal_when_fed;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
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

import static unaverage.tweaks.helper.AnimalsHealWhenFedKt.getHealsWhenFed;

@Mixin(AnimalEntity.class)
public abstract class AnimalMixin extends PassiveEntity {
    @Shadow public abstract boolean isBreedingItem(ItemStack stack);

    @Shadow protected abstract void eat(PlayerEntity player, Hand hand, ItemStack stack);

    @Shadow public abstract ActionResult interactMob(PlayerEntity player, Hand hand);

    protected AnimalMixin(EntityType<? extends PassiveEntity> entityType, World world) {super(entityType, world);}

    @Inject(
        method = "canEat",
        at = @At("RETURN"),
        cancellable = true
    )
    private void preventLoveModeWhenHurt(CallbackInfoReturnable<Boolean> cir) {
        if (!getHealsWhenFed(this.getType())) return;

        if (this.getHealth() < this.getMaxHealth()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
        method = "interactMob",
        at = @At("RETURN"),
        cancellable = true
    )
    private void healIfHurt(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!getHealsWhenFed(this.getType())) return;
        if (this.getType() == EntityType.PARROT) return;

        if (!this.isBreedingItem(player.getStackInHand(hand))) return;

        if (this.getHealth() < this.getMaxHealth()) {
            this.heal(1);
            this.eat(player, hand, player.getStackInHand(hand));

            cir.setReturnValue(
                ActionResult.SUCCESS
            );
        }
    }
}
