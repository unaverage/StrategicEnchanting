package unaverage.tweaks.helper

import net.minecraft.entity.EntityType
import net.minecraft.registry.Registries
import unaverage.tweaks.GlobalConfig
import kotlin.math.roundToInt

fun getFireProtectionLavaImmunityDuration(level: Int): Int {
    return GlobalConfig.fire_protection_offers_lava_immunity.seconds_of_lava_immunity_per_levels
        .let { it * level }
        .let{ it * 20 }
        .roundToInt()
}

val EntityType<*>.isFireProtectionAffected: Boolean
    get() {
        return GlobalConfig.fire_protection_offers_melee_protection.mobs_protected_against
            .containsRegex(
                this.getID(Registries.ENTITY_TYPE),
            )
    }