package unaverage.strategic_ench.mixin;

import net.minecraft.block.FrostedIceBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import unaverage.strategic_ench.config.GlobalConfig;

@Mixin(FrostedIceBlock.class)
public class FrostedIceMixin {
    @Redirect(
        method = "scheduledTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/world/ServerWorld;getLightLevel(Lnet/minecraft/util/math/BlockPos;)I"
        )
    )
    public int injectFrostedIceAlwaysMelts(ServerWorld instance, BlockPos pos){
        if (!GlobalConfig.Miscellaneous.frostwalker_melts_at_night) return instance.getLightLevel(pos);

        return 15;
    }
}
