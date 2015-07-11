package io.andrewohara.tinkertime.io.kspLauncher;

import io.andrewohara.tinkertime.models.ConfigData;

import java.util.Arrays;
import java.util.List;

public class WindowsExecStrategy extends GameExecStrategy {

	@Override
	protected List<String> getCommands(ConfigData config) {
		return Arrays.asList(config.getSelectedInstallation().getGameDataPath().resolve("../KSP.exe").toString());
	}
}