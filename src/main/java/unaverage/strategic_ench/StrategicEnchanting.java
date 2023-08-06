package unaverage.strategic_ench;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static unaverage.strategic_ench.config.GlobalConfigKt.runGlobalConfig;

public class StrategicEnchanting implements PreLaunchEntrypoint {
	public static final String MOD_ID = "strategic_ench";
	public static final Logger LOGGER = LoggerFactory.getLogger("capped");

	public static void logMissingID(String id){

	}

	public static void logInvalidConfig(String invalid){

	}

	@Override
	public void onPreLaunch() {
		runGlobalConfig();
	}
}
