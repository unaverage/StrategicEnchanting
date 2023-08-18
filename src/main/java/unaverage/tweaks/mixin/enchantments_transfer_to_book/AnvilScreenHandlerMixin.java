package unaverage.tweaks.mixin.enchantments_transfer_to_book;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import unaverage.tweaks.GlobalConfig;

import java.util.Collections;

import static unaverage.tweaks.HelperKt.cap;
import static unaverage.tweaks.HelperKt.getWeight;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    @Shadow private int repairItemUsage;

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(
        method = "updateResult",
        at = @At("RETURN")
    )
    void transfer_to_book(CallbackInfo ci){
        if (!GlobalConfig.enchantments_transfer_to_book.enable) return;

        if (!this.output.getStack(0).isEmpty()) return;

        var input1 = this.input.getStack(0);
        var input2 = this.input.getStack(1);

        if (!input1.isOf(Items.BOOK)) return;
        if (input1.getCount() != 1) return;
        if (!EnchantmentHelper.get(input1).isEmpty()) return;

        if (EnchantmentHelper.get(input2).isEmpty()) return;

        var result = Items.ENCHANTED_BOOK.getDefaultStack();
        var enchantments = EnchantmentHelper.get(input2);
        cap(
            enchantments,
            GlobalConfig.enchantments_transfer_to_book.getTransfer_percentage() * getWeight(enchantments),
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

        this.repairItemUsage = 0;
    }

    @Redirect(
        method = "onTakeOutput",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/inventory/Inventory;setStack(ILnet/minecraft/item/ItemStack;)V"
        )
    )
    void toolStays(Inventory instance, int i, ItemStack stack){
        if (!GlobalConfig.enchantments_transfer_to_book.enable) { instance.setStack(i, stack); return;}

        if (stack != ItemStack.EMPTY){ instance.setStack(i, stack); return;}

        var input1 = this.input.getStack(0);
        var input2 = this.input.getStack(1);

        if (!input1.isOf(Items.BOOK)) { instance.setStack(i, stack); return;}
        if (input1.getCount() != 1) { instance.setStack(i, stack); return;}
        if (!EnchantmentHelper.get(input1).isEmpty()) { instance.setStack(i, stack); return;}
        if (EnchantmentHelper.get(input2).isEmpty()) { instance.setStack(i, stack); return;}

        if (i == 1){
            this.input.setStack(0, ItemStack.EMPTY);

            EnchantmentHelper.set(
                Collections.emptyMap(),
                input2
            );
        }
    }
}
