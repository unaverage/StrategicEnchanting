package unaverage.strategic_ench.mixin;

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
import unaverage.strategic_ench.HelperKt;
import unaverage.strategic_ench.StrategicEnchanting;
import unaverage.strategic_ench.config.GlobalConfig;

import java.util.ArrayList;
import java.util.List;

import static unaverage.strategic_ench.config.GlobalConfigKt.configInitialized;

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

        if (!configInitialized) throw new RuntimeException("should not happen");

        if (GlobalConfig.Pig.INSTANCE.getExtraFood().isEmpty()) return;

        List<Item> result = new ArrayList<>();
        for (String id: GlobalConfig.Pig.INSTANCE.getExtraFood()){
            Item item = HelperKt.getItemFromId(id, Registries.ITEM);
            if (item == null){
                StrategicEnchanting.logMissingID(id);
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
