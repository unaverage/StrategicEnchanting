package unaverage.tweaks.helper

import net.minecraft.entity.EntityType
import net.minecraft.registry.Registries
import unaverage.tweaks.GlobalConfig

val EntityType<*>.healsWhenFed: Boolean
    get() {
        return GlobalConfig.animals_heal_when_fed.animals_affected
            .containsRegex(
                this.getID(Registries.ENTITY_TYPE)
            )
    }