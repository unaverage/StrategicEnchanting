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
			id + "in config is unknown or no longer used"
		);
	}

	public static void logInvalidConfigKey(String configKey){
		LOGGER.warn(
			configKey + " in config does not have a valid value"
		);
	}

	public static void logNonExistentConfigKey(String configKey){
		LOGGER.warn(
			configKey + " is an unknown key"
		);
	}

	@Override
	public void onPreLaunch() {
		runGlobalConfig();
	}
}
