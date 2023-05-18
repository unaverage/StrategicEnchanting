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
 * Contains configs relating to change to the bane of arthropod enchantment
 */
public class BaneOfArthropod {
    static final Map<String, Object> DEFAULT = Map.of(
        ExtraAffectedMobs.class.getSimpleName(), ExtraAffectedMobs.DEFAULT
    );

    final ExtraAffectedMobs extraAffectedMobs;

    BaneOfArthropod(Config c) {
        c = c.getConfig(this.getClass().getSimpleName());

        extraAffectedMobs = new ExtraAffectedMobs(c);
    }

    /**
     * {@return true if the entity is affected by bane of arthropods and was not affected in vanilla, false otherwise}
     * @param e The {@link EntityType} of the entity being tested
     */
    public boolean isExtraAffectedMob(EntityType<?> e){
        return Helper.contains(
            e,
            extraAffectedMobs.value,
            Registry.ENTITY_TYPE
        );
    }

    static class ExtraAffectedMobs {
        static final Set<String> DEFAULT = ImmutableSet.of(
            "minecraft:guardian",
            "minecraft:elder_guardian"
        );

        public Set<String> value;

        ExtraAffectedMobs(Config c) {
            value = new HashSet<>(
                c.getStringList(this.getClass().getSimpleName())
            );
        }
    }

    //TODO
    static class BreakCobWebFaster{
        static final boolean DEFAULT = true;

        final boolean value;

        BreakCobWebFaster(Config c){
            value = c.getBoolean(this.getClass().getSimpleName());
        }
    }

    //TODO
    static class LessCobWebSlowDown{
        static final boolean DEFAULT = true;

        final boolean value;

        LessCobWebSlowDown(Config c){
            value = c.getBoolean(this.getClass().getSimpleName());
        }
    }
}
