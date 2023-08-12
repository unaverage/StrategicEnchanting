package unaverage.tweaks.mixin;

import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static unaverage.tweaks.GlobalConfigKt.affectedByBaneOfArthropod;

@Mixin(DamageEnchantment.class)
public class DamageEnchantmentsMixin {
    @Shadow
    @Final
    public int typeIndex;

    /**
     * Adds the slowness effect to the configured mobs being targeted by bane of arthropods are done to normal arthropod mobs
     */
    @Inject(method = "onTargetDamaged", at=@At(value = "HEAD"))
    private void slowDownExtraMobs(LivingEntity user, Entity target, int level, CallbackInfo ci){
        if (!(target instanceof LivingEntity livingEntity)) return;
        if (this.typeIndex != 2) return;
        if (level == 0) return;

        if (!affectedByBaneOfArthropod(livingEntity.getType())) return;

        int i = 20 + user.getRandom().nextInt(10 * level);
        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, i, 3));
    }
}
