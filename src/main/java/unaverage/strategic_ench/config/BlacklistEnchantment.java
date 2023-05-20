package unaverage.strategic_ench.config;

import com.google.common.collect.ImmutableSet;
import com.typesafe.config.Config;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.registry.Registry;
import unaverage.strategic_ench.Helper;

import java.util.HashSet;
import java.util.Set;

/**
 * Contains configuration value relating to whether enchantments are blacklisted from appearing within the game
 */
public class BlacklistEnchantment {
    static Set<String> getDefault(boolean newFile){
        if (!newFile) return Set.of();

        return ImmutableSet.of(
            "minecraft:protection"
        );
    }

    final Set<String> current;

    BlacklistEnchantment(Config c) {
        current = new HashSet<>(
            c.getStringList(this.getClass().getSimpleName())
        );
    }

    /**
     * @param e The enchantment being tested
     * {@return True if the enchantment is blacklisted from appearing from the game, false otherwise}
     */
    public boolean isBlackListed(Enchantment e){
        return Helper.contains(
            e,
            current,
            Registry.ENCHANTMENT
        );
    }
}
