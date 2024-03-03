package unaverage.tweaks.helper

import net.minecraft.entity.EntityType
import net.minecraft.registry.Registries
import unaverage.tweaks.GlobalConfig

val EntityType<*>.isAffectedByBaneOfArthropods: Boolean
    get() {
        return GlobalConfig.bane_of_arthropods_affects_more_mobs.extra_mobs_affected
            .containsRegex(
                this.getID(Registries.ENTITY_TYPE)
            )
    }