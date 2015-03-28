package aohara.tinkertime.controllers.launcher;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import aohara.tinkertime.TinkerConfig;

abstract class GameExecStrategy {
	
	public abstract ProcessBuilder getExecCommand(TinkerConfig config) throws IOException;
	
	private static ProcessBuilder getProcessBuilder(TinkerConfig config, Path executablePath, String... preCommands){
		List<String> commands = new LinkedList<>();
		
		// Add any commands to be run before the executable is provided
		for (String command : preCommands){
			commands.add(command);
		}
		
		// Add executable command
		commands.add(executablePath.toString());
		
		// Add executable arguments if any are set in the config
		String args = config.getLaunchArguments();
		if (args != null && !args.trim().isEmpty()){
			commands.add(args);
		}

		return new ProcessBuilder(commands);
	}
	
	static class WindowsExecStrategy extends GameExecStrategy {

		@Override
		public ProcessBuilder getExecCommand(TinkerConfig config) throws IOException {
			Path path = config.getGameDataPath();
			path = path.resolve(config.use64BitGame() ? "../KSP_x64.exe" : "../KSP.exe");
			return getProcessBuilder(config, path);
		}
	}
	
	static class LinuxExecStrategy extends GameExecStrategy {

		@Override
		public ProcessBuilder getExecCommand(TinkerConfig config) throws IOException {
			Path path = config.getGameDataPath();
			path = path.resolve(config.use64BitGame() ? "../KSP.x86_64" : "../KSP.x86");
			return getProcessBuilder(config, path);
		}
	}
	
	static class MacExecStrategy extends GameExecStrategy {

		@Override
		public ProcessBuilder getExecCommand(TinkerConfig config) {
			Path path = config.getGameDataPath().resolve("../KSP.app");
			return getProcessBuilder(config, path, "open");
		}
	}
}
