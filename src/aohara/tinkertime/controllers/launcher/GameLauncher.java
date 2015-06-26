package aohara.tinkertime.controllers.launcher;

import java.io.IOException;

import com.google.inject.Inject;

import aohara.tinkertime.TinkerConfig;

public class GameLauncher {
	
	private final TinkerConfig config;
	private final GameExecStrategy strategy;
	
	@Inject
	GameLauncher(TinkerConfig config, GameExecStrategy launchStrategy){
		this.config = config;
		this.strategy = launchStrategy;
	}
	
	public void launchGame() throws IOException{
		strategy.getProcessBuilder(config).start();
	}
}
