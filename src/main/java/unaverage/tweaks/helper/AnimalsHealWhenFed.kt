package unaverage.tweaks.helper

import net.minecraft.entity.EntityType
import net.minecraft.util.registry.Registry
import unaverage.tweaks.GlobalConfig

val EntityType<*>.healsWhenFed: Boolean
    get() {
        return GlobalConfig.animals_heal_when_fed.animals_affected
            .containsRegex(
                this.getID(Registry.ENTITY_TYPE)
            )
    }