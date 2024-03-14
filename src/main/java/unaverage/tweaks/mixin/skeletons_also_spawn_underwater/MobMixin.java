package unaverage.tweaks.mixin.skeletons_also_spawn_underwater;

import kotlin.Pair;
import kotlin.random.RandomKt;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import unaverage.tweaks.GlobalConfig;

import static kotlin.random.PlatformRandomKt.asJavaRandom;
import static kotlin.random.RandomKt.*;
import static unaverage.tweaks.helper.HelperKt.passesChance;

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
        //Use kotlin's random as it gives better results for similar seeds
        var rng = asJavaRandom(Random(packID.hashCode()));

        //Returning early effectively prevents water skeletons from spawning by using the default canSpawn() behavior
        //Chance applies equally to skeletons in the same pack
        if (!passesChance(packChance, rng)) return;

        cir.setReturnValue(
            world.doesNotIntersectEntities(skeleton)
        );
    }
}
