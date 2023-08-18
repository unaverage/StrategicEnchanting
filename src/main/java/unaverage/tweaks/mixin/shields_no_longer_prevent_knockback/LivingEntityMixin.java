package unaverage.tweaks.mixin.shields_no_longer_prevent_knockback;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {super(type, world);}

    @Redirect(
        method = "takeShieldHit",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;knockback(Lnet/minecraft/entity/LivingEntity;)V"
        )
    )
    private void shieldsDoKnockback(LivingEntity instance, LivingEntity target){
        this.velocityModified = true;
    }
}
