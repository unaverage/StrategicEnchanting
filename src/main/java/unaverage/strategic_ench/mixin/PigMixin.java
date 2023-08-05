package unaverage.strategic_ench.mixin;

import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import unaverage.strategic_ench.HelperKt;
import unaverage.strategic_ench.StrategicEnchanting;
import unaverage.strategic_ench.config.GlobalConfig;

import java.util.ArrayList;
import java.util.List;

import static unaverage.strategic_ench.config.GlobalConfigKt.configInitialized;

@Mixin(PigEntity.class)
public class PigMixin {
    @Inject(
        method = "getSaddledSpeed",
        at = @At("RETURN"),
        cancellable = true
    )
    private void injectPigNewSpeed(PlayerEntity controllingPlayer, CallbackInfoReturnable<Float> cir){
        if (!configInitialized) return;

        double multiplier = GlobalConfig.Pig.INSTANCE.getSaddledSpeedMultiplier();

        if (multiplier == 1) return;

        cir.setReturnValue(
            (float)(cir.getReturnValue() * multiplier)
        );
    }
}
