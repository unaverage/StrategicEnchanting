package unaverage.tweaks.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ParrotEntity;
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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import unaverage.tweaks.HelperKt;

@Mixin(AnimalEntity.class)
public abstract class AnimalMixin extends PassiveEntity {
    @Shadow public abstract boolean isBreedingItem(ItemStack stack);

    @Shadow protected abstract void eat(PlayerEntity player, Hand hand, ItemStack stack);

    protected AnimalMixin(EntityType<? extends PassiveEntity> entityType, World world) {super(entityType, world);}


    @Redirect(
        method = "interactMob",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/passive/AnimalEntity;isBreedingItem(Lnet/minecraft/item/ItemStack;)Z"
        )
    )
    private boolean countNewFeedingItems(AnimalEntity instance, ItemStack stack){
        //noinspection ConstantConditions
        if ((Object)this instanceof ParrotEntity) return this.isBreedingItem(stack);

        var newFeedingItems = HelperKt.getNewAnimalFeedList(this.getType());
        if (newFeedingItems == null) return this.isBreedingItem(stack);

        return newFeedingItems.contains(stack.getItem());
    }

    @Inject(
        method = "canEat",
        at = @At("RETURN"),
        cancellable = true
    )
    private void preventLoveModeWhenHurt(CallbackInfoReturnable<Boolean> cir) {
        if (!HelperKt.healedWhenEat(this.getType())) return;

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
        if (!HelperKt.healedWhenEat(this.getType())) return;
        if (this.getType() == EntityType.PARROT) return;

        var itemInHand = player.getStackInHand(hand);

        var feedingList = HelperKt.getNewAnimalFeedList(this.getType());
        if (feedingList != null ? !feedingList.contains(itemInHand.getItem()) : !this.isBreedingItem(player.getStackInHand(hand))) return;

        if (this.getHealth() < this.getMaxHealth()) {
            this.heal(1);
            this.eat(player, hand, player.getStackInHand(hand));

            cir.setReturnValue(
                ActionResult.SUCCESS
            );
        }
    }
}
