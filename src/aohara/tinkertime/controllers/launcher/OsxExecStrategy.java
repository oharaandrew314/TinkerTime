package aohara.tinkertime.controllers.launcher;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import aohara.tinkertime.models.ConfigData;

public class OsxExecStrategy extends GameExecStrategy {

	@Override
	protected List<String> getCommands(ConfigData config) {
		Path path = config.getSelectedInstallation().getPath().resolve("../KSP.app");
		return Arrays.asList("open", path.toString());
	}
}
