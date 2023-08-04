package unaverage.strategic_ench.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.registry.Registries;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import unaverage.strategic_ench.config.GlobalConfig;

import static com.ibm.icu.text.PluralRules.Operand.e;
import static unaverage.strategic_ench.config.GlobalConfigKt.configInitialized;

@Mixin(ZombieEntity.class)
public abstract class ZombieMixin {
    @Inject(
        method = "createZombieAttributes",
        at = @At("RETURN")
    )
    private static void injectLessKnockback(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir){
        if (!configInitialized) throw new RuntimeException("should not happen");

        if (!GlobalConfig.Zombie.INSTANCE.getLessKnockBack()) return;

        cir.getReturnValue().add(
            EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, .75
        );
    }
}
