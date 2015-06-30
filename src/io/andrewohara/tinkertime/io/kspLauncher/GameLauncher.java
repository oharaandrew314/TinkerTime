package io.andrewohara.tinkertime.io.kspLauncher;

import io.andrewohara.tinkertime.models.ConfigFactory;

import java.io.IOException;

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
