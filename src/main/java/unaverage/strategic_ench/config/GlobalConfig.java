package unaverage.strategic_ench.config;

import com.typesafe.config.*;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;
import unaverage.strategic_ench.StrategicEnchanting;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * Contains all the configuration values of this mod
 */
public class GlobalConfig {
    /**
     * Is null if {@link GlobalConfig#run()} was not called
     */
    @Nullable
    public static GlobalConfig INSTANCE;

    static final ConfigRenderOptions RENDER_OPTION = ConfigRenderOptions
        .defaults()
        .setOriginComments(false);

    static final String FILE_NAME = StrategicEnchanting.MOD_ID + ".json";

    static final Config DEFAULT = ConfigFactory.parseMap(
        Map.of(
            EnchantmentCaps.class.getSimpleName(), EnchantmentCaps.DEFAULT,
            BlacklistEnchantment.class.getSimpleName(), BlacklistEnchantment.DEFAULT,
            FireProtection.class.getSimpleName(), FireProtection.DEFAULT,
            BaneOfArthropod.class.getSimpleName(), BaneOfArthropod.DEFAULT,
            Thorn.class.getSimpleName(), Thorn.DEFAULT
        )
    );


    public final EnchantmentCaps enchantmentCaps;
    public final BlacklistEnchantment blacklistEnchantment;
    public final FireProtection fireProtection;
    public final BaneOfArthropod baneOfArthropod;

    public final Thorn thorn;

    GlobalConfig(Config c){
        this.enchantmentCaps = new EnchantmentCaps(c);

        this.blacklistEnchantment = new BlacklistEnchantment(c);
        this.fireProtection = new FireProtection(c);
        this.baneOfArthropod = new BaneOfArthropod(c);
        this.thorn = new Thorn(c);
    }

    /**
     * Runs the configuration, setting all of the
     */
    public static void run() {
        try {
            var configPath = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
            var config = ConfigFactory.parseFile(configPath.toFile()).withFallback(DEFAULT);

            INSTANCE = new GlobalConfig(config);

            Files.writeString(
                configPath,
                config.root().render(RENDER_OPTION)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
