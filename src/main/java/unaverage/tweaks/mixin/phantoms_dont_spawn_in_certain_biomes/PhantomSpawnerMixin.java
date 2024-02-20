package unaverage.tweaks.mixin.phantoms_dont_spawn_in_certain_biomes;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.spawner.PhantomSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import unaverage.tweaks.GlobalConfig;

@Mixin(PhantomSpawner.class)
public class PhantomSpawnerMixin {
    @Redirect(
        method = "spawn",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/world/ServerWorld;getLocalDifficulty(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/LocalDifficulty;"
        )
    )
    LocalDifficulty preventPhantomFromSpawningAtMushroomIslands(ServerWorld instance, BlockPos blockPos){
        var biome = instance.getBiome(blockPos);

        for (var excluded: GlobalConfig.phantoms_dont_spawn_in_certain_biomes.getBiome_blacklist()){
            if (excluded.split(":").length != 2) continue;

            var namespace = excluded.split(":")[0];
            var path = excluded.split(":")[1];

            if (biome.matchesId(new Identifier(namespace, path))){
                return new LocalDifficulty(Difficulty.PEACEFUL, 0, 0, 0);
            }
        }

        return instance.getLocalDifficulty(blockPos);
    }
}
