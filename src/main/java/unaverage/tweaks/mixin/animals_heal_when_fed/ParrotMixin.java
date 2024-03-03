package unaverage.tweaks.mixin.animals_heal_when_fed;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

import static unaverage.tweaks.helper.AnimalsHealWhenFedKt.*;

@Mixin(ParrotEntity.class)
public abstract class ParrotMixin extends AnimalEntity {
    protected ParrotMixin(EntityType<? extends AnimalEntity> entityType, World world) {super(entityType, world);}

    @Shadow @Final private static Set<Item> TAMING_INGREDIENTS;

    @Inject(
        method = "interactMob",
        at = @At("HEAD"),
        cancellable = true
    )
    void injectIsBreedingItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir){
        if (!getHealsWhenFed(this.getType())) return;

        var itemInHand = player.getStackInHand(hand);
        if (!TAMING_INGREDIENTS.contains(itemInHand.getItem())) return;

        if (this.getHealth() < this.getMaxHealth()) {
            this.heal(1);
            this.eat(player, hand, itemInHand);

            cir.setReturnValue(
                ActionResult.SUCCESS
            );
        }
    }
}
