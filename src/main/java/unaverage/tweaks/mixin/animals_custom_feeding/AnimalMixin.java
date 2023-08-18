package unaverage.tweaks.mixin.animals_custom_feeding;

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
import org.spongepowered.asm.mixin.injection.Redirect;
import unaverage.tweaks.GlobalConfig;
import unaverage.tweaks.HelperKt;

@Mixin(AnimalEntity.class)
public abstract class AnimalMixin extends PassiveEntity {
    @Shadow public abstract boolean isBreedingItem(ItemStack stack);

    @Shadow public abstract ActionResult interactMob(PlayerEntity player, Hand hand);

    protected AnimalMixin(EntityType<? extends PassiveEntity> entityType, World world) {super(entityType, world);}

    @Redirect(
        method = "interactMob",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/passive/AnimalEntity;isBreedingItem(Lnet/minecraft/item/ItemStack;)Z"
        )
    )
    boolean countNewFeedingItems(AnimalEntity instance, ItemStack stack){
        if (!GlobalConfig.animals_custom_feeding.enable) return instance.isBreedingItem(stack);

        //noinspection ConstantConditions
        if ((Object)this instanceof ParrotEntity) return this.isBreedingItem(stack);

        var newFeedingItems = HelperKt.getNewFeedList(this.getType());
        if (newFeedingItems == null) return this.isBreedingItem(stack);

        return newFeedingItems.contains(stack.getItem());
    }
}
