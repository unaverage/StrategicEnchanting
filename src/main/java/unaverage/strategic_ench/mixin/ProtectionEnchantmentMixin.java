package unaverage.strategic_ench.mixin;

import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static unaverage.strategic_ench.config.GlobalConfigKt.fireProtectionProtectsAgainst;
import static unaverage.strategic_ench.config.GlobalConfigKt.configInitialized;

@Mixin(ProtectionEnchantment.class)
public class ProtectionEnchantmentMixin {
    @Shadow @Final public ProtectionEnchantment.Type protectionType;

    /**
     * Adds protection from a mob's melee attack if that mob is configured to be affected by the fire protection
     */
    @Inject(method = "getProtectionAmount", at = @At(value = "TAIL"), cancellable = true)
    private void injectFireProtectionExtraProtection(int level, DamageSource source, CallbackInfoReturnable<Integer> cir){
        if (!configInitialized) return;

        if (this.protectionType != ProtectionEnchantment.Type.FIRE) return;

        var attacker = source.getAttacker();
        if (attacker == null) return;

        if (!fireProtectionProtectsAgainst(attacker.getType())) return;

        cir.setReturnValue(level*2);
    }
}
