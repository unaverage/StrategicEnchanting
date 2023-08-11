package unaverage.tweaks;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static unaverage.tweaks.GlobalConfigKt.runGlobalConfig;

public class UnaverageTweaks implements PreLaunchEntrypoint {
	public static final String MOD_ID = "unaverage_tweaks";
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
