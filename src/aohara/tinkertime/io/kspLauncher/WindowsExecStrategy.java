package aohara.tinkertime.io.kspLauncher;

import java.util.Arrays;
import java.util.List;

import aohara.tinkertime.models.ConfigData;

public class WindowsExecStrategy extends GameExecStrategy {

	@Override
	protected List<String> getCommands(ConfigData config) {
		return Arrays.asList(config.getSelectedInstallation().getPath().resolve("../KSP.exe").toString());
	}
}