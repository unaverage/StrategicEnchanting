package unaverage.tweaks.mixin.bridging_is_disabled;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import unaverage.tweaks.HelperKt;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(
        method = "updateSupportingBlockPos",
        at = @At("HEAD")
    )
    void updateLastSupportingBlockWhenOffGround(boolean onGround, Vec3d movement, CallbackInfo ci){
        if (!((Object)this instanceof PlayerEntity player)) return;

        if (onGround) return;

        ((Entity)(Object)this)
            .supportingBlockPos
            .ifPresent(
                it -> HelperKt.setLastSupportingBlock(
                    player,
                    it
                )
            );
    }

    @Inject(
        method = "updateSupportingBlockPos",
        at = @At("RETURN")
    )
    void updateLastSupportingBlockWhenOnGround(boolean onGround, Vec3d movement, CallbackInfo ci){
        if (!((Object)this instanceof PlayerEntity player)) return;

        if (!onGround) return;

        ((Entity)(Object)this)
            .supportingBlockPos
            .ifPresent(
                it -> HelperKt.setLastSupportingBlock(
                    player,
                    it
                )
            );
    }
}
