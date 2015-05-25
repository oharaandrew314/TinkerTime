package aohara.tinkertime.controllers.launcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import aohara.common.OS;
import aohara.tinkertime.TinkerConfig;

abstract class GameExecStrategy {
	
	public boolean use64BitGame() throws IOException{
		switch(OS.getOs()){
		case Windows:
			// No 64-bit version in KSP >= 1.0
			return false;
		case Linux:
			// If Linux, run 64-bit if system is 64-bit
			try(
				BufferedReader r = new BufferedReader(new InputStreamReader(
					Runtime.getRuntime().exec("uname -m").getInputStream()
				))
			){
				return r.readLine().toLowerCase().equals("x86_64");
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		case Osx:
			// If OSX, always run 64-bit
			return true;
		default:
			throw new IllegalStateException();
		}
	}
	
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
			Path path = config.getGameDataPath().resolve("../KSP.exe");
			return getProcessBuilder(config, path);
		}
	}
	
	static class LinuxExecStrategy extends GameExecStrategy {

		@Override
		public ProcessBuilder getExecCommand(TinkerConfig config) throws IOException {
			Path path = config.getGameDataPath();
			path = path.resolve(use64BitGame() ? "../KSP.x86_64" : "../KSP.x86");
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
