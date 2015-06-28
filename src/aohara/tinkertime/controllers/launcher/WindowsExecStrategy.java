package aohara.tinkertime.controllers.launcher;

import java.util.Arrays;
import java.util.List;

import aohara.tinkertime.TinkerConfig;

public class WindowsExecStrategy extends GameExecStrategy {

	@Override
	protected List<String> getCommands(TinkerConfig config) {
		return Arrays.asList(config.getGameDataPath().resolve("../KSP.exe").toString());
	}
}