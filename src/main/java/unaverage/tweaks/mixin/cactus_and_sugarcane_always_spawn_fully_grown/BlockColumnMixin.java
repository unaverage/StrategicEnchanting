package unaverage.tweaks.mixin.cactus_and_sugarcane_always_spawn_fully_grown;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.intprovider.BiasedToBottomIntProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockColumnFeature.class)
public abstract class BlockColumnMixin {

    @Shadow
    public abstract boolean generate(FeatureContext<BlockColumnFeatureConfig> context);

    @Inject(
        method = "generate",
        at = @At(
            value = "HEAD"
        ),
        cancellable = true
    )
    private void makeCactus3Blocks(FeatureContext<BlockColumnFeatureConfig> context, CallbackInfoReturnable<Boolean> cir) {
        if (context.getConfig().layers().size() != 1) return;

        var config = context.getConfig().layers().get(0);
        if (!(config.state() instanceof SimpleBlockStateProvider)) return;
        if (!(config.height() instanceof BiasedToBottomIntProvider)) return;

        //prevents infinite recursion
        if (config.height().getMin() == 3 && config.height().getMax() == 4) return;

        var state = ((SimpleBlockStateProviderAccessor)config.state()).getState();
        if (!state.isOf(Blocks.CACTUS) && !state.isOf(Blocks.SUGAR_CANE)) return;

        cir.setReturnValue(
            this.generate(
                new FeatureContext<>(
                    context.getFeature(),
                    context.getWorld(),
                    context.getGenerator(),
                    context.getRandom(),
                    context.getOrigin(),
                    BlockColumnFeatureConfig.create(
                        BiasedToBottomIntProvider.create(3,4),
                        config.state()
                    )
                )
            )
        );
    }
}
