package unaverage.tweaks.mixin.glowstone_dust_can_make_signs_glow;

import net.minecraft.block.AbstractSignBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractSignBlock.class)
public class SignMixin {

    @Redirect(
        method = "onUse",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"
        )
    )
    boolean glowstone_can_make_signs_glow(ItemStack instance, Item item){
        if (item != Items.GLOW_INK_SAC) return instance.isOf(item);

        if (instance.getItem() == Items.GLOWSTONE_DUST) return true;

        return instance.isOf(item);
    }
}
