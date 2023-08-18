package unaverage.tweaks.mixin.enchantments_are_capped;

import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import unaverage.tweaks.GlobalConfig;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static unaverage.tweaks.HelperKt.cap;
import static unaverage.tweaks.HelperKt.getCapacity;
import static unaverage.tweaks.HelperKt.isBlackListed;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    /**
     * Injects enchantment capping whenever {@link EnchantmentHelper#set(Map, ItemStack)} is called
     * Does this by mutating the enchantment map parameter
     */
    @Inject(method = "set", at = @At("HEAD"))
    private static void capEnchantmentsOnSet(Map<Enchantment, Integer> enchantments, ItemStack stack, CallbackInfo ci){
        cap(
            enchantments,
            getCapacity(stack.getItem()),
            item->false
        );
    }

    /**
     * Injects the enchantment capping whenever {@link EnchantmentHelper#generateEnchantments(Random, ItemStack, int, boolean)} is called
     * Does this by mutating the list that's being returned
     */
    @Inject(method = "generateEnchantments", at = @At("RETURN"))
    private static void capEnchantmentsOnGenerate(Random random, ItemStack stack, int level, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir){
        if (!GlobalConfig.enchantments_are_capped.enable) return;

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

    /**
     * Prevents blacklisted enchantments from appearing in {@link EnchantmentHelper#generateEnchantments(Random, ItemStack, int, boolean)}
     */
    @Inject(method = "getPossibleEntries", at = @At("RETURN"))
    private static void removeBlacklistedEnchantments(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir){
        cir.getReturnValue().removeIf(
            e-> isBlackListed(e.enchantment)
        );
    }
}
