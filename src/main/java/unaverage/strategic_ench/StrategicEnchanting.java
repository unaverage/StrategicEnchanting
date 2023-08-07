package unaverage.strategic_ench;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static unaverage.strategic_ench.config.GlobalConfigKt.runGlobalConfig;

public class StrategicEnchanting implements PreLaunchEntrypoint {
	public static final String MOD_ID = "strategic_ench";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static void logMissingID(String id){
		LOGGER.warn(
			id + "is not a valid id"
		);
	}

	public static void logInvalidConfig(String configName){
		LOGGER.warn(
			configName + " in config does not have valid values"
		);
	}

	public static void logNonExistentConfig(String configName){
		LOGGER.warn(
			configName + " in config does not exist"
		);
	}

	@Override
	public void onPreLaunch() {
		runGlobalConfig();
	}
}
