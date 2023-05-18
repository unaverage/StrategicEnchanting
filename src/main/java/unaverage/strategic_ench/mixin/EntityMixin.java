package unaverage.strategic_ench.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import unaverage.strategic_ench.config.FireProtection;
import unaverage.strategic_ench.config.GlobalConfig;


@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract boolean isInLava();

    @Shadow public abstract void extinguish();

    @Unique
    private int lavaImmunityCountDown;
    @Unique
    private int lavaImmunityCoolDown;

    @Inject(method = "tick", at = @At("HEAD"))
    private void injectCoolDownCountDown(CallbackInfo ci){
        if (lavaImmunityCountDown > 0){
            lavaImmunityCountDown -= 1;
        }
        if (lavaImmunityCoolDown > 0){
            lavaImmunityCoolDown -= 1;
        }
    }

    //TODO there is a bug where the lava immunity will still apply even after the armor was removed
    /**
     * Temporarily grants lava immunity if lava immunity is enabled and that the entity has armor with fire protection
     * Does this by cancelling this function if those conditions are met
     */
    @Inject(method = "setOnFireFromLava", at = @At("HEAD"), cancellable = true)
    private void injectFireResBonus(CallbackInfo ci){
        if (GlobalConfig.INSTANCE == null) return;
        if (!GlobalConfig.INSTANCE.fireProtection.hasLavaImmunity()) return;
        //noinspection ConstantConditions

        if (!((Object)this instanceof LivingEntity thisAsLivingEntity)) return;

        //If cooldown is zero, then it can reapply the countdown for lava immunity
        if (this.lavaImmunityCoolDown == 0) {
            var levels = 0;
            for (var item : thisAsLivingEntity.getArmorItems()) {
                for (var el : EnchantmentHelper.get(item).entrySet()) {
                    var e = el.getKey();
                    var l = el.getValue();

                    if (e != Enchantments.FIRE_PROTECTION) continue;

                    levels += l;
                }
            }
            if (levels == 0) return;

            var duration = GlobalConfig.INSTANCE.fireProtection.getLavaImmunityDuration(levels);

            this.lavaImmunityCountDown = duration;

            thisAsLivingEntity.addStatusEffect(
                new StatusEffectInstance(
                    StatusEffects.FIRE_RESISTANCE,
                    duration,
                    0, false, true, false
                )
            );

            this.lavaImmunityCoolDown = FireProtection.COOL_DOWN_SEC * 20;
        }

        //actually applies the lava immunity if the countdown is not zero
        if (this.lavaImmunityCountDown != 0){
            ci.cancel();
        }
    }
}
