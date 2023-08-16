package unaverage.tweaks.mixin.piglins_and_hoglins_fire_immune;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.PiglinEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import unaverage.tweaks.GlobalConfig;

@Mixin(EntityType.class)
public class EntityTypeMixin {
    @Mutable @Final @Shadow
    private boolean fireImmune;

    @Shadow @Final
    public static EntityType<PiglinEntity> PIGLIN;

    @Shadow @Final
    public static EntityType<HoglinEntity> HOGLIN;

    @Inject(
        method = "<clinit>",
        at = @At("TAIL")
    )
    private static void fn(CallbackInfo ci){
        if (!GlobalConfig.piglins_and_hoglins_are_fire_immune) return;

        ((EntityTypeMixin)(Object)PIGLIN).fireImmune = true;
        ((EntityTypeMixin)(Object)HOGLIN).fireImmune = true;
    }
}
