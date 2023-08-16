package unaverage.tweaks.mixin.xp;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import unaverage.tweaks.GlobalConfig;

@Mixin(ExperienceOrbEntity.class)
abstract class XpOrbMixin extends Entity {
    public XpOrbMixin(EntityType<?> type, World world) {super(type, world);}
    @Shadow private int amount;

    @Inject(
        method = "tick",
        at = @At("HEAD"),
        cancellable = true
    )
    void removeFromExistence(CallbackInfo ci){
        if (!GlobalConfig.xp.disable_xp) return;

        this.amount = 0;
        this.discard();

        ci.cancel();
    }
}