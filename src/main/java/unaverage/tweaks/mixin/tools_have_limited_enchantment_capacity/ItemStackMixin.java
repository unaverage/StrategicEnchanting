package unaverage.tweaks.mixin.tools_have_limited_enchantment_capacity;

import net.minecraft.client.item.TooltipType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import unaverage.tweaks.GlobalConfig;

import java.util.List;

import static unaverage.tweaks.helper.HelperKt.getTotalWeight;
import static unaverage.tweaks.helper.HelperKt.toMap;
import static unaverage.tweaks.helper.ToolsHaveLimitedEnchantmentCapacityKt.getCapacity;
import static unaverage.tweaks.helper.ToolsHaveLimitedEnchantmentCapacityKt.toStringWithDecimalPlaces;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    public abstract Item getItem();

    @Inject(
        method = "getTooltip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/Item;appendTooltip(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/Item$TooltipContext;Ljava/util/List;Lnet/minecraft/client/item/TooltipType;)V",
            shift = At.Shift.AFTER
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void showCapInfo(Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> cir, List<Text> list) {
        var self = (ItemStack) (Object) this;

        var enchantments = toMap(EnchantmentHelper.getEnchantments(self));
        if (enchantments.isEmpty()) return;

        var weight = getTotalWeight(enchantments);
        var cap = getCapacity(self.getItem());
        if (cap == null || cap < 0) return;

        int toolTipDecimalPlaces = GlobalConfig.tools_have_limited_enchantment_capacity.getTool_tip_decimal_places();

        list.add(
            Text.translatable(
                "unaverage_tweaks.enchantment_capacity",
                toStringWithDecimalPlaces(weight, toolTipDecimalPlaces),
                toStringWithDecimalPlaces(cap, toolTipDecimalPlaces)
            )
        );
    }
}