package aohara.tinkertime.controllers.launcher;

import java.io.IOException;

import aohara.tinkertime.TinkerConfig;

public class GameLauncher {
	
	private final TinkerConfig config;
	private final GameExecStrategy strategy;
	
	public GameLauncher(TinkerConfig config, GameExecStrategy launchStrategy){
		this.config = config;
		this.strategy = launchStrategy;
	}
	
	public static GameLauncher create(TinkerConfig config){
		return new GameLauncher(config, getExecStrategy());
	}
	
	public void launchGame() throws IOException{
		strategy.getExecCommand(config).start();
	}
	
	private static GameExecStrategy getExecStrategy(){
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")){
			return new GameExecStrategy.WindowsExecStrategy();
		} else if (os.contains("mac")){
			return new GameExecStrategy.MacExecStrategy();
		} else if (os.contains("nux")){
			return new GameExecStrategy.LinuxExecStrategy();
		} else {
			throw new IllegalStateException("Cannot recognise os: " + os);
		}
	}
}
