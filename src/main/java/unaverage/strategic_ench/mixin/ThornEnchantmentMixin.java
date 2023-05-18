package unaverage.strategic_ench.mixin;

import net.minecraft.enchantment.ThornsEnchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import unaverage.strategic_ench.config.GlobalConfig;

import java.util.function.Consumer;

@Mixin(ThornsEnchantment.class)
public class ThornEnchantmentMixin {

    /**
     * Disables the default vanilla behavior of damaging armor if thorn inflict damage in enemies
     */
    @ModifyConstant(
        method = "onUserDamaged",
        constant = @Constant(intValue = 2)
    )
    private <T extends LivingEntity> int doNoDamage(int constant){
        if (GlobalConfig.INSTANCE == null) return constant;

        return GlobalConfig.INSTANCE.thorn.stopArmorDamage() ? 0 : constant;
    }
}
