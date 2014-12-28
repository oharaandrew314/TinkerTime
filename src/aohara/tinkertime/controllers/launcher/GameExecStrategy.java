package aohara.tinkertime.controllers.launcher;

import java.nio.file.Path;

import aohara.tinkertime.TinkerConfig;

interface GameExecStrategy {
	
	public Path getPath(TinkerConfig config);
	
	
	class WindowsExecStrategy implements GameExecStrategy {

		@Override
		public Path getPath(TinkerConfig config) {
			Path path = config.getGameDataPath();
			return path.resolve(config.use64BitGame() ? "../KSP_x64.exe" : "../KSP.exe");
		}
	}
	
	class LinuxExecStrategy implements GameExecStrategy {

		@Override
		public Path getPath(TinkerConfig config) {
			Path path = config.getGameDataPath();
			return path.resolve(config.use64BitGame() ? "../KSP.x86_64" : "../KSP.x86");
		}
	}
	
	class MacExecStrategy implements GameExecStrategy {

		@Override
		public Path getPath(TinkerConfig config) {
			return config.getGameDataPath().resolve("../KSP.app/KSP");
		}
	}
}
