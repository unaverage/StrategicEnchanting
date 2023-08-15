package unaverage.tweaks.mixin;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import unaverage.tweaks.GlobalConfig;
import unaverage.tweaks.HelperKt;

import java.util.List;

import static unaverage.tweaks.HelperKt.*;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract Item getItem();

    @Shadow public abstract int getMaxDamage();

    @Shadow public abstract boolean hasEnchantments();

    @Inject(
        method = "getTooltip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;appendEnchantments(Ljava/util/List;Lnet/minecraft/nbt/NbtList;)V",
            shift = At.Shift.AFTER
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void showCapInfo(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir, List<Text> list) {
        var self = (ItemStack)(Object)this;

        var enchantments = EnchantmentHelper.get(self);
        if (enchantments.isEmpty()) return;
        
        var weight = getWeight(enchantments);
        var cap = getCapacity(self.getItem());
        if (cap == null || cap < 0) return;

        var text = "Enchantment Capacity: %s/%s".formatted(
            HelperKt.toString(weight, GlobalConfig.EnchantmentCaps.tool_tip_decimal_places),
            HelperKt.toString(cap, GlobalConfig.EnchantmentCaps.tool_tip_decimal_places)
        );

        list.add(
            MutableText.of(new LiteralTextContent(text))
        );
    }

    @Inject(
        method = "addEnchantment",
        at = @At(value = "RETURN")
    )
    private void injectCappingOnAddEnch(Enchantment enchantment, int level, CallbackInfo ci){
        var enchantments = EnchantmentHelper.get((ItemStack)(Object)this);

        capEnchantmentMap(
            enchantments,
            getCapacity(this.getItem()),
            item->false
        );

        EnchantmentHelper.set(enchantments, (ItemStack)(Object)this);
    }

    /**
     * Injects capping at {@link ItemStack#setNbt(NbtCompound)} of an item
     * Setting the nbt of an item from another is a way to transfer enchantments between those items
     * An example is done by smithing tables upgrading a diamond tool to its perspective netherite tool
     * The problem is that these different tools can have different capacities, and must be recapped
     * This is done by capping the enchantment of the tool after {@link ItemStack#setNbt(NbtCompound)} has finished
     */
    @Inject(
        method = "setNbt",
        at = @At(value = "RETURN")
    )
    private void injectCappingOnSetNBT(NbtCompound nbt, CallbackInfo ci){
        var enchantments = EnchantmentHelper.get((ItemStack)(Object)this);

        //I'm not sure why this fixes the issue with EnchantedShulker mod
        //I'm also not sure why this doesn't work in the dev environment, only the real environment
        if (enchantments.isEmpty()) return;

        capEnchantmentMap(
            enchantments,
            getCapacity(this.getItem()),
            item->false
        );

        //This function is probably the root cause of the problem with EnchantedShulker
        EnchantmentHelper.set(enchantments, (ItemStack)(Object)this);
    }


    @Inject(
        method = "getMaxDamage",
        at = @At("HEAD"),
        cancellable = true
    )
    void useNewToolDecay(CallbackInfoReturnable<Integer> cir){
        if (GlobalConfig.XP.tools_decay_rate <= 0) return;
        if (GlobalConfig.XP.tools_max_decay < 0) return;
        if (GlobalConfig.XP.tools_decay_rate == 1) return;

        if (!this.hasEnchantments()) return;

        var totalRepairs = HelperKt.getTimesRepaired((ItemStack)(Object)this);
        var initDurability = this.getMaxDamage();

        var decayRate = GlobalConfig.XP.tools_decay_rate;
        var maxDecay = GlobalConfig.XP.tools_max_decay;

        //uses the finite geometric series, excluding the first term
        //   s=ar^1+ar^2+...+ar^n
        //or s=a(1-r^(n+1))/(1-r)-a
        //where
        //a is the initial durability
        //r is the decay rate
        //n is the total full repairs done
        //s is the total durability repaired

        //the formula for maxDurability is m=ar^n

        //solve for n in the first equation,
        //then plug it in on the second equation,
        //then simplify to get this result
        var result = initDurability - decayRate/(1-decayRate)*totalRepairs;

        if (result < initDurability*maxDecay){
            result = initDurability*maxDecay;
        }
        //durability should never be 0
        if (result < 1){
            result = 1;
        }

        cir.setReturnValue(
            (int)Math.round(result)
        );
    }
}
