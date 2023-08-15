package unaverage.tweaks.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import unaverage.tweaks.GlobalConfig;
import unaverage.tweaks.HelperKt;

import java.util.Map;

import static unaverage.tweaks.HelperKt.capEnchantmentMap;
import static unaverage.tweaks.HelperKt.getCapacity;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    @Shadow private int repairItemUsage;

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(
        method = "canTakeOutput",
        at = @At("HEAD"),
        cancellable = true
    )
    void removeXpRequirement(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> cir){
        if (!GlobalConfig.XP.anvil_no_longer_requires_xp) return;

        cir.setReturnValue(true);
    }


    @Inject(
        method = "updateResult",
        at = @At("TAIL")
    )
    void preventDecaySwapCheating(CallbackInfo ci){
        if (GlobalConfig.XP.tool_decay_rate <= 0) return;

        var input1 = this.input.getStack(0);
        var input2 = this.input.getStack(1);

        if (input1.getItem() != input2.getItem()) return;

        var result = this.output.getStack(0);
        var resultDamage  = result.getDamage();

        var decay1 = HelperKt.getDecay(input1);
        var decay2 = HelperKt.getDecay(input2);

        //undos the vanilla repair, but set the initial decay to whichever tool has the most decay
        result.setDamage(input1.getDamage());
        HelperKt.setDecay(result, Math.max(decay1, decay2));

        //repairs the item all over again, but with the new initial decay value
        result.setDamage(resultDamage);
    }

    @Inject(
        method = "updateResult",
        at = @At("TAIL")
    )
    void useNewRepair(CallbackInfo ci){
        var input1 = this.input.getStack(0);
        var input2 = this.input.getStack(1);
        var result = this.output.getStack(0);

        var ingotsToFullyRepair = HelperKt.getIngotsToFullyRepair(input1.getItem());
        if (ingotsToFullyRepair == null) return;

        if (!input1.getItem().canRepair(input1, input2)) return;
        if (input1.getItem() == input2.getItem()) return;

        //undos the vanilla repair
        result.setDamage(input1.getDamage());
        HelperKt.setDecay(result, HelperKt.getDecay(input1));
        this.repairItemUsage = 0;

        //repairs the item all over again
        for (int i = 0; i < input2.getCount(); i++){
            if (result.getDamage() == 0) break;

            var repairPerIngots = result.getMaxDamage() / ingotsToFullyRepair;

            result.setDamage(result.getDamage() - repairPerIngots);

            this.repairItemUsage += 1;
        }
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
    private void anvilUsesDifferentCappingBehavior(Map<Enchantment, Integer> enchantments, ItemStack stack){
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
