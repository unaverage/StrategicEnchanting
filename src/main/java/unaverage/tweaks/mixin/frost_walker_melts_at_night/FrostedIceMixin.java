package unaverage.tweaks.mixin.frost_walker_melts_at_night;

import net.minecraft.block.FrostedIceBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FrostedIceBlock.class)
public class FrostedIceMixin {
    @Redirect(
        method = "scheduledTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/world/ServerWorld;getLightLevel(Lnet/minecraft/util/math/BlockPos;)I"
        )
    )
    public int meltFrostedIceAtNight(ServerWorld instance, BlockPos pos){
        return 15;
    }
}
