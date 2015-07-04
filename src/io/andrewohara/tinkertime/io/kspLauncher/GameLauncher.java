package io.andrewohara.tinkertime.io.kspLauncher;

import io.andrewohara.tinkertime.models.ConfigData;

import java.io.IOException;

import com.google.inject.Inject;

public class GameLauncher {

	private final ConfigData config;
	private final GameExecStrategy strategy;

	@Inject
	GameLauncher(ConfigData config, GameExecStrategy launchStrategy){
		this.config = config;
		this.strategy = launchStrategy;
	}

	public void launchGame() throws IOException{
		strategy.getProcessBuilder(config).start();
	}
}
