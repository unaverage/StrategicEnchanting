package unaverage.strategic_ench.mixin;

import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.IronGolemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import unaverage.strategic_ench.config.GlobalConfig;

import java.util.function.Predicate;

@Mixin(ActiveTargetGoal.class)
public abstract class ActiveTargetMixin extends TrackTargetGoal {
    @Shadow protected TargetPredicate targetPredicate;

    public ActiveTargetMixin(MobEntity mob, boolean checkVisibility) {super(mob, checkVisibility);}

    @Inject(
        method = "<init>(Lnet/minecraft/entity/mob/MobEntity;Ljava/lang/Class;IZZLjava/util/function/Predicate;)V",
        at = @At("TAIL")
    )
    public void injectMobsDontAttackVillagerGolems(MobEntity mob, Class<?> targetClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, Predicate<?> targetPredicate, CallbackInfo ci){
        if (GlobalConfig.Miscellaneous.village_golems_only_fight_threats) return;

        if (mob instanceof ZombieEntity) return;
        if (mob instanceof PatrolEntity) return;

        if (targetClass != IronGolemEntity.class) return;

        this.targetPredicate = this.targetPredicate.setPredicate(
            e -> ((IronGolemEntity)e).isPlayerCreated()
        );
    }

    @Inject(
        method = "<init>(Lnet/minecraft/entity/mob/MobEntity;Ljava/lang/Class;IZZLjava/util/function/Predicate;)V",
        at = @At("TAIL")
    )
    public void injectVillagerGolemsDontAttackMobs(MobEntity mob, Class<?> targetClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, Predicate<?> targetPredicate, CallbackInfo ci){
        if (!GlobalConfig.Miscellaneous.village_golems_only_fight_threats) return;

        if (!(mob instanceof IronGolemEntity)) return;

        if (targetClass != MobEntity.class) return;

        this.targetPredicate = this.targetPredicate.setPredicate(
            e -> {
                if (!((IronGolemEntity)mob).isPlayerCreated()){
                    return e instanceof ZombieEntity || e instanceof PatrolEntity;
                }

                if (!(e instanceof Monster)) return false;
                if (e instanceof CreeperEntity) return false;

                return true;
            }
        );
    }
}
