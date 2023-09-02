package unaverage.tweaks.mixin.thorns_no_longer_wear_down_armor;

import net.minecraft.enchantment.ThornsEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ThornsEnchantment.class)
public class ThornsEnchantmentMixin {
    /**
     * Disables the default vanilla behavior of damaging armor if thorn inflict damage in enemies
     */
    @ModifyConstant(
        method = "onUserDamaged",
        constant = @Constant(intValue = 2)
    )
    private int cancelArmorWearDown(int constant){
        return 0;
    }
}
