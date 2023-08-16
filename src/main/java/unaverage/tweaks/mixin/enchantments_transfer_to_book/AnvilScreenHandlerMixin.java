package unaverage.tweaks.mixin.enchantments_transfer_to_book;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import unaverage.tweaks.GlobalConfig;

import static unaverage.tweaks.HelperKt.cap;
import static unaverage.tweaks.HelperKt.getWeight;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(
        method = "updateResult",
        at = @At("TAIL")
    )
    void transfer_to_book(CallbackInfo ci){
        if (!GlobalConfig.enchantments_transfer_to_book.enable) return;

        var input1 = this.input.getStack(0);
        var input2 = this.input.getStack(1);

        if (!input1.isEnchantable()) return;
        if (!input2.isOf(Items.BOOK)) return;
        if (input2.getCount() != 1) return;
        if (!this.output.getStack(0).isEmpty()) return;

        if (EnchantmentHelper.get(input1).isEmpty()) return;
        if (!EnchantmentHelper.get(input2).isEmpty()) return;

        var result = input2.copy();

        var enchantments = EnchantmentHelper.get(input1);

        cap(
            enchantments,
            GlobalConfig.enchantments_transfer_to_book.transfer_percentage * getWeight(enchantments),
            x->false
        );

        EnchantmentHelper.set(
            enchantments,
            result
        );

        this.output.setStack(
            0,
            result
        );
    }
}
