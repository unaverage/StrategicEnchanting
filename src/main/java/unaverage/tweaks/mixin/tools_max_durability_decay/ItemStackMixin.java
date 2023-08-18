package unaverage.tweaks.mixin.tools_max_durability_decay;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import unaverage.tweaks.GlobalConfig;
import unaverage.tweaks.HelperKt;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract Item getItem();

    @Shadow public abstract int getDamage();

    @Inject(
        method = "setDamage",
        at = @At("HEAD")
    )
    void decayWhenRepaired(int newDamage, CallbackInfo ci){
        if (!GlobalConfig.tools_max_durability_decay.enable) return;

        var oldDamage = this.getDamage();
        if (oldDamage <= newDamage) return;

        var diff = oldDamage - newDamage;

        var totalEnchantments = HelperKt.getWeight(EnchantmentHelper.get((ItemStack) (Object)this));
        if (totalEnchantments <= 0) return;

        var decay = (double)diff / (double) GlobalConfig.tools_max_durability_decay.getDecay_rate() * totalEnchantments;

        HelperKt.setDecay(
            (ItemStack)(Object)this,
            HelperKt.getDecay((ItemStack)(Object)this) + decay
        );
    }

    @Inject(
        method = "getMaxDamage",
        at = @At("HEAD"),
        cancellable = true
    )
    void getMaxDamageWithDecay(CallbackInfoReturnable<Integer> cir){
        if (!GlobalConfig.tools_max_durability_decay.enable) return;

        var decay = (int)Math.floor(HelperKt.getDecay((ItemStack)(Object)this));
        if (decay <= 0) return;

        var result = this.getItem().getMaxDamage() - decay;
        if (result < 1){
            result = 1;
        }

        cir.setReturnValue(result);
    }

}
