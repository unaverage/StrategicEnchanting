package unaverage.tweaks.mixin.enchantments_can_transfer_to_books;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import unaverage.tweaks.GlobalConfig;

import java.util.Collections;

import static unaverage.tweaks.helper.HelperKt.cap;
import static unaverage.tweaks.helper.HelperKt.getTotalWeight;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

    @Shadow @Final private Property levelCost;

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(
        method = "updateResult",
        at = @At("RETURN")
    )
    void transfer_to_book(CallbackInfo ci){
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
            GlobalConfig.enchantments_can_transfer_to_books.getTransfer_percentage() * getTotalWeight(enchantments),
            x->false
        );
        if (enchantments.isEmpty()) return;

        EnchantmentHelper.set(
            enchantments,
            result
        );

        this.output.setStack(
            0,
            result
        );

        this.levelCost.set(1);
    }

    @Redirect(
        method = "onTakeOutput",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/inventory/Inventory;setStack(ILnet/minecraft/item/ItemStack;)V"
        )
    )
    void toolStays(Inventory instance, int i, ItemStack stack){
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
