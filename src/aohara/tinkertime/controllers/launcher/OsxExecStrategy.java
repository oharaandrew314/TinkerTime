package aohara.tinkertime.controllers.launcher;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import aohara.tinkertime.TinkerConfig;

public class OsxExecStrategy extends GameExecStrategy {

	@Override
	protected List<String> getCommands(TinkerConfig config) {
		Path path = config.getGameDataPath().resolve("../KSP.app");
		return Arrays.asList("open", path.toString());
	}
}
