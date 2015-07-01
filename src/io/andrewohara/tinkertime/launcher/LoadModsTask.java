package io.andrewohara.tinkertime.launcher;

import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateCoordinatorImpl;
import io.andrewohara.tinkertime.db.ConfigFactory;

import com.google.inject.Inject;

public class LoadModsTask implements Runnable {

	private final ModUpdateCoordinatorImpl updateCooridnator;
	private final ConfigFactory configFactory;

	@Inject
	LoadModsTask(ModUpdateCoordinatorImpl updateCooridnator, ConfigFactory configFactory){
		this.updateCooridnator = updateCooridnator;
		this.configFactory = configFactory;
	}

	@Override
	public void run() {
		updateCooridnator.changeInstallation(configFactory.getConfig().getSelectedInstallation());
	}

}
