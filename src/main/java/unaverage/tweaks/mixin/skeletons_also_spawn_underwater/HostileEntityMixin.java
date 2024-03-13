package unaverage.tweaks.mixin.skeletons_also_spawn_underwater;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.world.Heightmap;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpawnRestriction.class)
public class HostileEntityMixin {
    @Inject(
        method = "getHeightmapType",
        at = @At(value = "TAIL"),
        cancellable = true
    )
    private static void letSkeletonsSpawnOnOceanFloor(@Nullable EntityType<?> type, CallbackInfoReturnable<Heightmap.Type> cir){
        if (type != EntityType.SKELETON) return;

        cir.setReturnValue(Heightmap.Type.OCEAN_FLOOR);
    }

    @Inject(
        method = "getLocation",
        at = @At(value = "TAIL"),
        cancellable = true
    )
    private static void letSkeletonsSpawnInWater(@Nullable EntityType<?> type, CallbackInfoReturnable<SpawnRestriction.Location> cir){
        if (type != EntityType.SKELETON) return;

        cir.setReturnValue(SpawnRestriction.Location.NO_RESTRICTIONS);
    }
}
