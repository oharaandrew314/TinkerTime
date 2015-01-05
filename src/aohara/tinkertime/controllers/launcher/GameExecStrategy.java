package aohara.tinkertime.controllers.launcher;

import java.nio.file.Path;

import aohara.tinkertime.TinkerConfig;

interface GameExecStrategy {
	
	public ProcessBuilder getExecCommand(TinkerConfig config);
	
	
	class WindowsExecStrategy implements GameExecStrategy {

		@Override
		public ProcessBuilder getExecCommand(TinkerConfig config) {
			Path path = config.getGameDataPath();
			path = path.resolve(config.use64BitGame() ? "../KSP_x64.exe" : "../KSP.exe");
			if (config.useBorderlessWindow()) {
				return new ProcessBuilder(path.toString(), "-popupwindow");
			} else {
				return new ProcessBuilder(path.toString());
			}
		}
	}
	
	class LinuxExecStrategy implements GameExecStrategy {

		@Override
		public ProcessBuilder getExecCommand(TinkerConfig config) {
			Path path = config.getGameDataPath();
			path = path.resolve(config.use64BitGame() ? "../KSP.x86_64" : "../KSP.x86");
			return new ProcessBuilder(path.toString());
		}
	}
	
	class MacExecStrategy implements GameExecStrategy {

		@Override
		public ProcessBuilder getExecCommand(TinkerConfig config) {
			Path path = config.getGameDataPath().resolve("../KSP.app");
			return new ProcessBuilder("open", path.toString());
		}
	}
}
