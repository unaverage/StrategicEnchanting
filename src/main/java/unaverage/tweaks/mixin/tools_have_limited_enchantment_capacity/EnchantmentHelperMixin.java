package unaverage.tweaks.mixin.tools_have_limited_enchantment_capacity;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static unaverage.tweaks.helper.HelperKt.cap;
import static unaverage.tweaks.helper.ToolsHaveLimitedEnchantmentCapacityKt.getCapacity;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    /**
     * Injects the enchantment capping whenever {@link EnchantmentHelper#generateEnchantments(FeatureSet, Random, ItemStack, int, boolean)} is called
     * Does this by mutating the list that's being returned
     */
    @Inject(method = "generateEnchantments", at = @At("RETURN"))
    private static void capEnchantmentsOnGenerate(FeatureSet enabledFeatures, Random random, ItemStack stack, int level, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir){
        var originalList = cir.getReturnValue();

        //converts the list of EnchantmentLevelEntries to an enchantment map
        var result = originalList.stream().collect(
            Collectors.toMap(
                o->o.enchantment,
                o->o.level
            )
        );

        //caps the enchantment map
        cap(
            result,
            getCapacity(stack.getItem()),
            item->false
        );

        //clears the list, then refills it with the capped enchantments
        originalList.clear();
        result.forEach(
            (e, l) -> originalList.add(new EnchantmentLevelEntry(e, l))
        );
    }
}
