package unaverage.tweaks.mixin.tools_max_durability_will_decay;

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

import static unaverage.tweaks.helper.ToolMaxDurabilityWillDecayKt.getDecay;
import static unaverage.tweaks.helper.ToolMaxDurabilityWillDecayKt.setDecay;

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
    void transferDecay(CallbackInfo ci){
        var input1 = this.input.getStack(0);
        var input2 = this.input.getStack(0);
        var result = this.output.getStack(0);

        var decay = Math.max(
            getDecay(input1),
            getDecay(input2)
        );

        setDecay(result, decay);
    }
}
