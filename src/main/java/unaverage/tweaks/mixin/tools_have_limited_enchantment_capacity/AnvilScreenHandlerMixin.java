package unaverage.tweaks.mixin.tools_have_limited_enchantment_capacity;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

import static unaverage.tweaks.helper.HelperKt.*;
import static unaverage.tweaks.helper.ToolsHaveLimitedEnchantmentCapacityKt.getCapacity;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {super(type, syncId, playerInventory, context);}

    /**
     * Redirects the {@link EnchantmentHelper#set(Map, ItemStack)} that would have used the default capping behavior, and instead use a different capping behavior
     * The default capping behavior would not have prioritized the enchantment in the sacrifice item
     * The new capping behavior will prioritize the enchantments that are from the sacrificed item in the anvil
     */
    @Redirect(
        method = "updateResult",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/enchantment/EnchantmentHelper;set(Lnet/minecraft/item/ItemStack;Lnet/minecraft/component/type/ItemEnchantmentsComponent;)V"
        )
    )
    void anvilUsesDifferentCappingBehavior(ItemStack stack, ItemEnchantmentsComponent enchantments){
        var inputFromSecondSlot = this.input.getStack(1);

        var map = toMap(enchantments);

        //applies the new capping behavior
        cap(
            map,
            getCapacity(stack.getItem()),
            //prioritize the enchantment if its from the sacrifice item
            e -> toMap(EnchantmentHelper.getEnchantments(inputFromSecondSlot)).containsKey(e)
        );

        EnchantmentHelper.set(stack, toComponent(map));
    }
}
