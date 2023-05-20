package unaverage.strategic_ench.config;

import com.google.common.collect.ImmutableSet;
import com.typesafe.config.Config;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;
import unaverage.strategic_ench.Helper;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Contains all the configuration values relating to the fire protection enchantment
 */
public class FireProtection {
    public static int COOL_DOWN_SEC = 30;
    static Map<String, Object> getDefault(boolean newFile){
        return Map.of(
            SecondsOfLavaImmunityPerLevel.class.getSimpleName(), SecondsOfLavaImmunityPerLevel.DEFAULT,
            AddMobProtection.class.getSimpleName(), AddMobProtection.getDefault(newFile)
        );
    }

    SecondsOfLavaImmunityPerLevel secondsOfLavaImmunityPerLevel;
    AddMobProtection addMobProtection;

    FireProtection(Config c) {
        c = c.getConfig(this.getClass().getSimpleName());

        secondsOfLavaImmunityPerLevel = new SecondsOfLavaImmunityPerLevel(c);
        addMobProtection = new AddMobProtection(c);
    }

    /**
     * @param t The entity type of the mob being tested
     * {@return true if fire protection is configured to protect against this entity's melee attacks}
     */
    public boolean protectsAgainst(EntityType<?> t){
        return Helper.contains(
            t,
            addMobProtection.value,
            Registry.ENTITY_TYPE
        );
    }

    /**
     * {@return true if temporary lava immunity is enabled}
     */
    public boolean hasLavaImmunity(){
        return secondsOfLavaImmunityPerLevel.value > 0;
    }

    /**
     * {@return the duration in ticks of the temporary laval immunity}
     */
    public int getLavaImmunityDuration(int level){
        return (int)Math.round(secondsOfLavaImmunityPerLevel.value * 20) * level;
    }

    static class SecondsOfLavaImmunityPerLevel {
        static final double DEFAULT = 1.0;

        Double value;

        SecondsOfLavaImmunityPerLevel(Config c) {
            value = c.getDouble(this.getClass().getSimpleName());
        }
    }

    static class AddMobProtection {
        static Set<String> getDefault(boolean newFile){
            if (!newFile) return Set.of();

            return ImmutableSet.of(
                "minecraft:blaze",
                "minecraft:magma_cube"
            );
        }

        Set<String> value;

        AddMobProtection(Config c) {
            value = new HashSet<>(
                c.getStringList(this.getClass().getSimpleName())
            );
        }
    }
}
