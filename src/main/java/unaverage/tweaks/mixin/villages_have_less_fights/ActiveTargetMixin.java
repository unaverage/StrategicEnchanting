package unaverage.tweaks.mixin.villages_have_less_fights;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(ActiveTargetGoal.class)
public abstract class ActiveTargetMixin extends TrackTargetGoal {
    @Shadow protected TargetPredicate targetPredicate;

    public ActiveTargetMixin(MobEntity mob, boolean checkVisibility) {super(mob, checkVisibility);}

    @Inject(
        method = "<init>(Lnet/minecraft/entity/mob/MobEntity;Ljava/lang/Class;IZZLjava/util/function/Predicate;)V",
        at = @At("TAIL")
    )
    public void cancelMobToVillageGolemTargeting(MobEntity mob, Class<?> targetClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, Predicate<?> targetPredicate, CallbackInfo ci){
        if (mob instanceof ZombieEntity) return;
        if (mob instanceof RaiderEntity) return;

        if (targetClass != IronGolemEntity.class) return;

        this.targetPredicate = this.targetPredicate.setPredicate(
            e -> ((IronGolemEntity)e).isPlayerCreated()
        );
    }

    @Inject(
        method = "<init>(Lnet/minecraft/entity/mob/MobEntity;Ljava/lang/Class;IZZLjava/util/function/Predicate;)V",
        at = @At("TAIL")
    )
    public void cancelVillagerGolemsToMobTargeting(MobEntity mob, Class<?> targetClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, Predicate<LivingEntity> originalPredicate, CallbackInfo ci){
        if (!(mob instanceof IronGolemEntity golem)) return;

        if (targetClass != MobEntity.class) return;

        this.targetPredicate = this.targetPredicate.setPredicate(
            e -> {
                //unfortunately, the golem can't be tested if it's village-made much earlier
                if (golem.isPlayerCreated()){
                    return originalPredicate.test(e);
                }

                if (e instanceof ZombieEntity) return true;
                if (e instanceof RaiderEntity) return true;

                var targetOfTarget = ((MobEntity)e).getTarget();
                if (targetOfTarget instanceof IronGolemEntity) return true;
                if (targetOfTarget instanceof VillagerEntity) return true;

                return false;
            }
        );
    }
}
