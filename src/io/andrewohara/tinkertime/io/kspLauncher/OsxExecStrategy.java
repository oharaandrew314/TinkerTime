package io.andrewohara.tinkertime.io.kspLauncher;

import io.andrewohara.tinkertime.models.ConfigData;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class OsxExecStrategy extends GameExecStrategy {

	@Override
	protected List<String> getCommands(ConfigData config) {
		Path path = config.getSelectedInstallation().getGameDataPath().resolve("../KSP.app");
		return Arrays.asList("open", path.toString());
	}
}
