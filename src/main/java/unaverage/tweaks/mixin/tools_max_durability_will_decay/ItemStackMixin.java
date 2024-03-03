package unaverage.tweaks.mixin.tools_max_durability_will_decay;

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

import static unaverage.tweaks.helper.HelperKt.*;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract Item getItem();

    @Shadow public abstract int getDamage();

    @Inject(
        method = "setDamage",
        at = @At("HEAD")
    )
    void decayWhenRepaired(int newDamage, CallbackInfo ci){
        var oldDamage = this.getDamage();
        if (oldDamage <= newDamage) return;

        var diff = oldDamage - newDamage;

        var totalEnchantments = getTotalWeight(EnchantmentHelper.get((ItemStack) (Object)this));
        if (totalEnchantments <= 0) return;

        var decay = (double)diff / (double) GlobalConfig.tools_max_durability_will_decay.getDecay_rate() * totalEnchantments;

        unaverage.tweaks.helper.ToolMaxDurabilityWillDecayKt.setDecay(
            (ItemStack)(Object)this,
            unaverage.tweaks.helper.ToolMaxDurabilityWillDecayKt.getDecay((ItemStack)(Object)this) + decay
        );
    }

    @Inject(
        method = "getMaxDamage",
        at = @At("HEAD"),
        cancellable = true
    )
    void getMaxDamageWithDecay(CallbackInfoReturnable<Integer> cir){
        var decay = (int)Math.floor(unaverage.tweaks.helper.ToolMaxDurabilityWillDecayKt.getDecay((ItemStack)(Object)this));
        if (decay <= 0) return;

        var result = this.getItem().getMaxDamage() - decay;
        if (result < 1){
            result = 1;
        }

        cir.setReturnValue(result);
    }

}
