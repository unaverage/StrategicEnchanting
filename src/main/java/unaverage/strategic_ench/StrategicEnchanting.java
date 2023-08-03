package unaverage.strategic_ench;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static unaverage.strategic_ench.config.GlobalConfigKt.runGlobalConfig;

public class StrategicEnchanting implements ModInitializer {
	public static final String MOD_ID = "strategic_ench";
	public static final Logger LOGGER = LoggerFactory.getLogger("capped");

	@Override
	public void onInitialize() {
		runGlobalConfig();
	}
}
