package io.andrewohara.tinkertime.launcher;

import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateCoordinator;
import io.andrewohara.tinkertime.models.ConfigFactory;

import com.google.inject.Inject;

public class LoadModsTask implements Runnable {

	private final ModUpdateCoordinator updateCooridnator;
	private final ConfigFactory configFactory;

	@Inject
	LoadModsTask(ModUpdateCoordinator updateCooridnator, ConfigFactory configFactory){
		this.updateCooridnator = updateCooridnator;
		this.configFactory = configFactory;
	}

	@Override
	public void run() {
		updateCooridnator.changeInstallation(configFactory.getConfig().getSelectedInstallation());
	}

}
