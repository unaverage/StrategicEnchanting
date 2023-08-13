package unaverage.tweaks.mixin;

import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import unaverage.tweaks.HelperKt;

@Mixin(TemptGoal.class)
public class TemptMixin {

    @Mutable
    @Shadow @Final private Ingredient food;

    @Inject(
        method = "<init>",
        at = @At("TAIL")
    )
    void mobsEatExtraFood(PathAwareEntity entity, double speed, Ingredient food, boolean canBeScared, CallbackInfo ci){
        //TODO better way to exclude nonbreeding tempt goals
        if (food.test(Items.CARROT_ON_A_STICK.getDefaultStack())) return;
        if (food.test(Items.WARPED_FUNGUS_ON_A_STICK.getDefaultStack())) return;

        var list = HelperKt.getNewAnimalFeedList(entity.getType());
        if (list == null) return;


        this.food = Ingredient.ofStacks(list.stream().map(Item::getDefaultStack));
    }
}
