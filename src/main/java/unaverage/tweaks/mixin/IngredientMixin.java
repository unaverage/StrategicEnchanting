package unaverage.tweaks.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import unaverage.tweaks.HelperKt;
import unaverage.tweaks.UnaverageTweaks;
import unaverage.tweaks.config.GlobalConfig;

import java.util.ArrayList;
import java.util.List;

@Mixin(Ingredient.class)
public abstract class IngredientMixin {
    @Inject(
        method = "ofItems",
        at = @At("RETURN"),
        cancellable = true
    )
    private static void injectPigNewIngredients(ItemConvertible[] items, CallbackInfoReturnable<Ingredient> cir){
        if (items.length != 3) return;
        if (items[0] != Items.CARROT) return;
        if (items[1] != Items.POTATO) return;
        if (items[2] != Items.BEETROOT) return;

        if (GlobalConfig.Miscellaneous.pigs_eat.isEmpty()) return;

        List<Item> result = new ArrayList<>();
        for (String id: GlobalConfig.Miscellaneous.pigs_eat){
            Item item = HelperKt.getItemFromId(id, Registries.ITEM);
            if (item == null){
                UnaverageTweaks.logMissingID(id);
            }
            else {
                result.add(item);
            }
        }

        cir.setReturnValue(
            Ingredient.ofStacks(result.stream().map(ItemStack::new))
        );
    }
}
