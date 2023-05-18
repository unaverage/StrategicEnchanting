package unaverage.strategic_ench.mixin;

import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import unaverage.strategic_ench.config.GlobalConfig;

@Mixin(ProtectionEnchantment.class)
public class ProtectionEnchantmentMixin {
    @Shadow @Final public ProtectionEnchantment.Type protectionType;

    /**
     * Adds protection from a mob's melee attack if that mob is configured to be affected by the fire protection
     */
    @Inject(method = "getProtectionAmount", at = @At(value = "TAIL"), cancellable = true)
    private void injectFireProtectionExtraProtection(int level, DamageSource source, CallbackInfoReturnable<Integer> cir){
        if (this.protectionType != ProtectionEnchantment.Type.FIRE) return;

        if (!(source instanceof EntityDamageSource e)) return;

        var attacker = e.getAttacker().getType();

        if (GlobalConfig.INSTANCE == null) return;
        if (!GlobalConfig.INSTANCE.fireProtection.protectsAgainst(attacker)) return;

        cir.setReturnValue(level*2);
    }
}
