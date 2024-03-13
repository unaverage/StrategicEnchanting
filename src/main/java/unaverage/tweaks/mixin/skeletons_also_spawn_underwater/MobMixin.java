package unaverage.tweaks.mixin.skeletons_also_spawn_underwater;

import kotlin.Pair;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import unaverage.tweaks.GlobalConfig;
import unaverage.tweaks.helper.HelperKt;
import unaverage.tweaks.helper.SkeletonsAlsoSpawnUnderwaterKt;

import static unaverage.tweaks.helper.HelperKt.*;
import static unaverage.tweaks.helper.SkeletonsAlsoSpawnUnderwaterKt.*;

@Mixin(MobEntity.class)
public class MobMixin {
    @Inject(
        method = "canSpawn(Lnet/minecraft/world/WorldView;)Z",
        at = @At(value = "HEAD"),
        cancellable = true
    )
    void letSkeletonsSpawnInWater(WorldView world, CallbackInfoReturnable<Boolean> cir){
        if (!((Object)this instanceof SkeletonEntity skeleton)) return;

        //skeletons from the same pack gets the same hash
        var packHash = getPackHash(skeleton);
        var packChance = GlobalConfig.skeletons_also_spawn_underwater.getSpawn_chance();

        //effectively prevents water skeletons from spawning by using the default canSpawn() behavior
        //Chance applies equally to skeletons in the same pack
        if (!passesChance(packChance, packHash)) return;

        cir.setReturnValue(
            world.doesNotIntersectEntities(skeleton)
        );
    }
}
