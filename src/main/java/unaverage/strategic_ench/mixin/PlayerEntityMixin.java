package unaverage.strategic_ench.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static unaverage.strategic_ench.config.GlobalConfigKt.affectedByBaneOfAnthropod;
import static unaverage.strategic_ench.config.GlobalConfigKt.configInitialized;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Unique
    private Entity targetParam = null;

    /**
     * I'm not sure how to grab the local parameter in a redirect
     * A duct-tape fix would be to use an inject mixin to grab it, then store it in a variable
     */
    @Inject(
        method = "attack",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/enchantment/EnchantmentHelper;getAttackDamage(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityGroup;)F",
            shift = At.Shift.BEFORE
        )
    )
    private void grabTargetParam(Entity target, CallbackInfo ci){
        this.targetParam = target;
    }

    /**
     * Applies extra damage to mobs that are configured to be effected by the bane of arthropods
     * I'm not sure how to directly modify the damage local variable in {@link PlayerEntity#attack(Entity)}
     * So instead, I redirected a function that modifies that local variable to indirectly modify that variable
     */
    @Redirect(
        method = "attack",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/enchantment/EnchantmentHelper;getAttackDamage(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityGroup;)F"
        )
    )
    private float addExtraMobDamage(ItemStack stack, EntityGroup group){
        var self = (PlayerEntity) (Object)this;

        //the original result
        var result = EnchantmentHelper.getAttackDamage(stack, group);

        if (!configInitialized) return result;

        if (!affectedByBaneOfAnthropod(this.targetParam.getType())) return result;

        var level = EnchantmentHelper.getEquipmentLevel(Enchantments.BANE_OF_ARTHROPODS, self);
        if (level == 0) return result;

        return result + level * 2.5f;
    }
}
