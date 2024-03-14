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

import java.util.Random;

import static unaverage.tweaks.helper.HelperKt.*;

@Mixin(MobEntity.class)
public class MobMixin {
    @Inject(
        method = "canSpawn(Lnet/minecraft/world/WorldView;)Z",
        at = @At(value = "HEAD"),
        cancellable = true
    )
    void letSkeletonsSpawnInWater(WorldView world, CallbackInfoReturnable<Boolean> cir){
        if (!((Object)this instanceof SkeletonEntity skeleton)) return;

        var packChance = GlobalConfig.skeletons_also_spawn_underwater.getSpawn_chance();

        //skeletons from the same pack get the same id
        var packID = new Pair<>(skeleton.getChunkPos(), skeleton.getWorld().getTime());

        //Make sure that skeletons from the same pack have the same rng
        var rng = new Random(packID.hashCode());

        //Returning early effectively prevents water skeletons from spawning by using the default canSpawn() behavior
        //Chance applies equally to skeletons in the same pack
        if (!passesChance(packChance, rng)) return;

        cir.setReturnValue(
            world.doesNotIntersectEntities(skeleton)
        );
    }
}
