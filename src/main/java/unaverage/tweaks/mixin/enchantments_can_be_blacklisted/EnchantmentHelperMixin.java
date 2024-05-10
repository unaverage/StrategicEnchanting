package unaverage.tweaks.mixin.enchantments_can_be_blacklisted;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static unaverage.tweaks.helper.EnchantmentsCanBeBlacklistedKt.isBlackListed;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Inject(method = "getPossibleEntries", at = @At("RETURN"))
    private static void removeBlacklistedEnchantments(FeatureSet enabledFeatures, int level, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir){
        cir.getReturnValue().removeIf(
            e-> isBlackListed(e.enchantment)
        );
    }
}
