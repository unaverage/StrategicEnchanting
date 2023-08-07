package unaverage.strategic_ench.mixin;

import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.OcelotEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import unaverage.strategic_ench.config.GlobalConfig;

import java.util.function.Predicate;

@Mixin(FleeEntityGoal.class)
public abstract class FleeEntityMixin{

    @Mutable
    @Shadow @Final protected float fleeDistance;

    @Inject(
        method = "<init>(Lnet/minecraft/entity/mob/PathAwareEntity;Ljava/lang/Class;Ljava/util/function/Predicate;FDDLjava/util/function/Predicate;)V",
        at = @At("TAIL")
    )
    public void injectCreepersFleeFurther(PathAwareEntity mob, Class<?> fleeFromType, Predicate<?> extraInclusionSelector, float distance, double slowSpeed, double fastSpeed, Predicate<?> inclusionSelector, CallbackInfo ci){
        if (GlobalConfig.Miscellaneous.creepers_avoid_cats_further_away) return;

        if (!(mob instanceof CreeperEntity)) return;
        if (fleeFromType != CatEntity.class && fleeFromType != OcelotEntity.class) return;

        this.fleeDistance = 16;
    }
}
