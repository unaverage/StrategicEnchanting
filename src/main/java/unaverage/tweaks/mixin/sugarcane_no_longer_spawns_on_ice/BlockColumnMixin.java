package unaverage.tweaks.mixin.sugarcane_no_longer_spawns_on_ice;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.intprovider.BiasedToBottomIntProvider;
import net.minecraft.world.gen.feature.BlockColumnFeature;
import net.minecraft.world.gen.feature.BlockColumnFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockColumnFeature.class)
public abstract class BlockColumnMixin {

    @Inject(
        method = "generate",
        at = @At(
            value = "HEAD"
        ),
        cancellable = true
    )
    private void makeSugarCaneNoLongerSpawnOnIce(FeatureContext<BlockColumnFeatureConfig> context, CallbackInfoReturnable<Boolean> cir) {
        if (context.getConfig().layers().size() != 1) return;

        var config = context.getConfig().layers().get(0);
        if (!(config.state() instanceof SimpleBlockStateProvider)) return;
        if (!(config.height() instanceof BiasedToBottomIntProvider)) return;

        var state = ((SimpleBlockStateProviderAccessor)config.state()).getState();
        if (!state.isOf(Blocks.SUGAR_CANE)) return;

        if (context.getWorld().getBiome(context.getOrigin()).value().doesNotSnow(context.getOrigin())) return;

        cir.setReturnValue(true);
    }
}
