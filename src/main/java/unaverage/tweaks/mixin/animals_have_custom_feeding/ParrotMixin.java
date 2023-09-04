package unaverage.tweaks.mixin.animals_have_custom_feeding;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import unaverage.tweaks.HelperKt;

import java.util.Set;

@Mixin(ParrotEntity.class)
public abstract class ParrotMixin extends AnimalEntity {
    protected ParrotMixin(EntityType<? extends AnimalEntity> entityType, World world) {super(entityType, world);}

    @Shadow @Final private static Set<Item> TAMING_INGREDIENTS;

    @Inject(
        method = "<clinit>",
        at = @At("TAIL")
    )
    private static void canBeTamedWithNewFeedingList(CallbackInfo ci){
        var list = HelperKt.getNewFeedList(EntityType.PARROT);
        if (list == null) return;

        TAMING_INGREDIENTS.clear();
        TAMING_INGREDIENTS.addAll(list);
    }
}
