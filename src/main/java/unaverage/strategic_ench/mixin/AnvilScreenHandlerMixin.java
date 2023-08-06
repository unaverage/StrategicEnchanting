package unaverage.strategic_ench.mixin;

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

import static unaverage.strategic_ench.HelperKt.capEnchantmentMap;
import static unaverage.strategic_ench.HelperKt.getCapacity;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }


    /**
     * Redirects the {@link EnchantmentHelper#set(Map, ItemStack)} that would have used the default capping behavior, and instead use a different capping behavior
     * The default capping behavior would not have prioritized the enchantment in the sacrifice item
     * The new capping behavior will prioritize the enchantments that are from the sacrificed item in the anvil
     */
    @Redirect(
        method = "updateResult",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/enchantment/EnchantmentHelper;set(Ljava/util/Map;Lnet/minecraft/item/ItemStack;)V"
        )
    )
    private void redirectDefaultCappingToNewCapping(Map<Enchantment, Integer> enchantments, ItemStack stack){
        /*
        if (!configInitialized) {
            //equivalent of doing no redirects at all if the global config isn't accessible yet
            EnchantmentHelper.set(enchantments, stack);
            return;
        }

         */

        var inputFromSecondSlot = this.input.getStack(1);

        //applies the new capping behavior
        capEnchantmentMap(
            enchantments,
            getCapacity(stack.getItem()),
            //prioritize the enchantment if its from the sacrifice item
            e -> EnchantmentHelper.get(inputFromSecondSlot).containsKey(e)
        );

        //EnchantmentHelper#set will still perform the default capping behavior, but it doesnt matter because it is already capped by the previous capping behavior
        EnchantmentHelper.set(enchantments, stack);
    }
}
