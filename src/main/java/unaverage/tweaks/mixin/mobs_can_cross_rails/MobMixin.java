package unaverage.tweaks.mixin.mobs_can_cross_rails;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobMixin  {
    @Shadow public abstract void setPathfindingPenalty(PathNodeType nodeType, float penalty);

    @Inject(
        method = "<init>",
        at = @At("TAIL")
    )
    void onInitTail(EntityType<?> entityType, World world, CallbackInfo ci){
        this.setPathfindingPenalty(
            PathNodeType.UNPASSABLE_RAIL,
            0.0f
        );
    }
}
