package unaverage.tweaks.mixin.enchantments_can_transfer_to_books;

import net.minecraft.component.type.ItemEnchantmentsComponent;
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

import static unaverage.tweaks.helper.HelperKt.*;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

    @Shadow @Final private Property levelCost;

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    //TODO simplify this with helper functions
    @Inject(
        method = "updateResult",
        at = @At("RETURN")
    )
    void transferToBook(CallbackInfo ci){
        if (!this.output.getStack(0).isEmpty()) return;

        var inputBook = this.input.getStack(0);
        var inputTool = this.input.getStack(1);

        //check that it's a book
        if (!inputBook.isOf(Items.BOOK)) return;

        //check that there is only one book
        if (inputBook.getCount() != 1) return;

        //Check that the book does not have any enchantment
        if (!EnchantmentHelper.getEnchantments(inputBook).isEmpty()) return;

        //Check that the tool has enchantments
        if (EnchantmentHelper.getEnchantments(inputTool).isEmpty()) return;

        var result = Items.ENCHANTED_BOOK.getDefaultStack();

        //get and cap the enchantments from the tool
        var enchantments = toMap(EnchantmentHelper.getEnchantments(inputTool));

        cap(
            enchantments,
            GlobalConfig.enchantments_can_transfer_to_books.getTransfer_percentage() * getTotalWeight(enchantments),
            x->false
        );

        //do nothing if no enchantment can be transferred
        if (enchantments.isEmpty()) return;

        EnchantmentHelper.set(result, toComponent(enchantments));

        this.output.setStack(0, result);

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

        var inputBook = this.input.getStack(0);
        var inputTool = this.input.getStack(1);

        //Run the default setStack behavior if it's not transferring anything to a book
        if (!inputBook.isOf(Items.BOOK)) { instance.setStack(i, stack); return;}
        if (inputBook.getCount() != 1) { instance.setStack(i, stack); return;}
        if (!EnchantmentHelper.getEnchantments(inputBook).isEmpty()) { instance.setStack(i, stack); return;}
        if (EnchantmentHelper.getEnchantments(inputTool).isEmpty()) { instance.setStack(i, stack); return;}

        if (i == 1){
            this.input.setStack(0, ItemStack.EMPTY);

            EnchantmentHelper.set(
                inputTool,
                ItemEnchantmentsComponent.DEFAULT
            );
        }
    }
}
