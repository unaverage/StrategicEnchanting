package unaverage.tweaks.mixin.endermen_can_teleport_unreachable_players;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import unaverage.tweaks.helper.HelperKt;

import static unaverage.tweaks.helper.HelperKt.*;

@Mixin(EndermanEntity.class)
public abstract class EndermanMixin extends HostileEntity {
    protected EndermanMixin(EntityType<? extends HostileEntity> entityType, World world) {super(entityType, world);}

    @Unique
    private int coolDown = 0;

    @Inject(
        method = "tickMovement",
        at = @At(
            value = "HEAD"
        )
    )
    void tryTeleportUnreachablePlayers(CallbackInfo ci){
        if (coolDown > 0){
            coolDown -= 1;
            return;
        }

        if (!(this.getTarget() instanceof PlayerEntity player)){
            return;
        }

        var dist = this.getPos().distanceTo(player.getPos());

        if (dist > 3.5){
            return;
        }
        if (dist < 1.5){
            return;
        }

        var blockOverHead = this.getWorld().getBlockState(
            player.getBlockPos().up().up()
        );

        if (!blockOverHead.isOpaque()) return;

        player.teleport(this.getPos().x, this.getPos().y, this.getPos().z);
        this.getWorld().emitGameEvent(GameEvent.TELEPORT, this.getPos(), GameEvent.Emitter.of(this));
        this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

        this.coolDown = 3 * TICKS_PER_SEC;
    }
}
