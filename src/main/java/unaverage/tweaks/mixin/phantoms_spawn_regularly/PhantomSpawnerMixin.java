package unaverage.tweaks.mixin.phantoms_spawn_regularly;

import net.minecraft.world.GameRules;
import net.minecraft.world.spawner.PhantomSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PhantomSpawner.class)
public class PhantomSpawnerMixin {
    @Redirect(
        method = "spawn",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"
        )
    )
    boolean insomniaIsOn(GameRules instance, GameRules.Key<GameRules.BooleanRule> rule){
        return true;
    }

    @Redirect(
        method = "spawn",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/math/MathHelper;clamp(III)I"
        )
    )
    int playerHasNotSlept(int value, int min, int max){
        return Integer.MAX_VALUE;
    }
}
