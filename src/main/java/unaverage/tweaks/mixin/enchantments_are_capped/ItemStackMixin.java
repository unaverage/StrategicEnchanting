package unaverage.tweaks.mixin.enchantments_are_capped;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import unaverage.tweaks.GlobalConfig;
import unaverage.tweaks.HelperKt;

import java.util.List;

import static unaverage.tweaks.HelperKt.getCapacity;
import static unaverage.tweaks.HelperKt.getWeight;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    public abstract Item getItem();

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
        var self = (ItemStack) (Object) this;

        var enchantments = EnchantmentHelper.get(self);
        if (enchantments.isEmpty()) return;

        var weight = getWeight(enchantments);
        var cap = getCapacity(self.getItem());
        if (cap == null || cap < 0) return;

        var text = "Enchantment Capacity: %s/%s".formatted(
            HelperKt.toStringWithDecimalPlaces(weight, GlobalConfig.tools_have_limited_enchantment_capacity.getTool_tip_decimal_places()),
            HelperKt.toStringWithDecimalPlaces(cap, GlobalConfig.tools_have_limited_enchantment_capacity.getTool_tip_decimal_places())
        );

        list.add(
            MutableText.of(new LiteralTextContent(text))
        );
    }
}