package unaverage.tweaks.mixin.tools_custom_repair_rate;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import unaverage.tweaks.GlobalConfig;
import unaverage.tweaks.HelperKt;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    @Shadow private int repairItemUsage;

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(
        method = "updateResult",
        at = @At("TAIL")
    )
    void useNewRepair(CallbackInfo ci){
        if (!GlobalConfig.tools_custom_repair_rate.enable) return;

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
}
