package aohara.tinkertime.io.kspLauncher;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import aohara.tinkertime.models.ConfigData;

public abstract class GameExecStrategy {

	public ProcessBuilder getProcessBuilder(ConfigData config) throws IOException{
		List<String> commands = new LinkedList<>();

		// Add any commands to run the executable
		for (String command : getCommands(config)){
			commands.add(command);
		}

		// Add executable arguments if any are set in the config
		String args = config.getLaunchArguments();
		if (args != null && !args.trim().isEmpty()){
			commands.add(args);
		}

		return new ProcessBuilder(commands);
	}

	protected abstract List<String> getCommands(ConfigData config);
}
