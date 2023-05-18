package unaverage.strategic_ench.config;

import com.typesafe.config.Config;

import java.util.Map;

/**
 * Contains configuration values relating to the thorns enchantment
 */
public class Thorn {
    static final Map<String, Object> DEFAULT = Map.of(
        StopArmorDamage.class.getSimpleName(), StopArmorDamage.DEFAULT
    );

    final StopArmorDamage stopArmorDamage;
    Thorn(Config c) {
        c = c.getConfig(this.getClass().getSimpleName());

        stopArmorDamage = new StopArmorDamage(c);
    }

    /**
     * {@return true if the thorns protection should not damage armor after inflicting damage, false otherwise}
     */
    public boolean stopArmorDamage(){
        return stopArmorDamage.value;
    }

    static class StopArmorDamage {
        static final boolean DEFAULT = true;
        public Boolean value;

        StopArmorDamage(Config c) {
            this.value = c.getBoolean(this.getClass().getSimpleName());
        }
    }
}
