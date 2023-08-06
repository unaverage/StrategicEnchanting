package unaverage.strategic_ench.mixin;
/*
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.ZombieEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import unaverage.strategic_ench.config.GlobalConfig;


@Mixin(ZombieEntity.class)
public abstract class ZombieMixin {

    @Inject(
        method = "createZombieAttributes",
        at = @At("RETURN")
    )
    private static void injectLessKnockback(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir){
        if (!GlobalConfig.Miscellaneous.zombies_are_more_knockback_resistant) return;

        cir.getReturnValue().add(
            EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, .75
        );
    }
}*/
