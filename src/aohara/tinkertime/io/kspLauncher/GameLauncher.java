package aohara.tinkertime.io.kspLauncher;

import java.io.IOException;

import aohara.tinkertime.models.ConfigFactory;

import com.google.inject.Inject;

public class GameLauncher {

	private final ConfigFactory configFactory;
	private final GameExecStrategy strategy;

	@Inject
	GameLauncher(ConfigFactory configFactory, GameExecStrategy launchStrategy){
		this.configFactory = configFactory;
		this.strategy = launchStrategy;
	}

	public void launchGame() throws IOException{
		strategy.getProcessBuilder(configFactory.getConfig()).start();
	}
}
