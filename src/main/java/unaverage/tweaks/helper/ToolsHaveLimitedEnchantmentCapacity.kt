package unaverage.tweaks.helper

import net.minecraft.item.Item
import net.minecraft.util.registry.Registry
import unaverage.tweaks.GlobalConfig
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * {@return The total weights of enchantments the item can hold}
 */
val Item.capacity: Double?
    get() {
        return GlobalConfig.tools_have_limited_enchantment_capacity.item_capacities
            .getWithRegex(
                this.getID(Registry.ITEM)
            )
    }

fun Double.toStringWithDecimalPlaces(decimalPlace: Int): String {
    return BigDecimal(this)
        .setScale(decimalPlace, RoundingMode.FLOOR)
        .toString()
}