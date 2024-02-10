package unaverage.tweaks.mixin.sleeping_heals;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerMixin extends LivingEntity {
    @Shadow public abstract boolean damage(DamageSource source, float amount);

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, World world) {super(entityType, world);}

    @Inject(
        method = "wakeUp(ZZ)V",
        at = @At("HEAD")
    )
    void healOnWakeUp(boolean skipSleepTimer, boolean updateSleepingPlayers, CallbackInfo ci){
        this.setHealth(this.getMaxHealth());
    }
}
