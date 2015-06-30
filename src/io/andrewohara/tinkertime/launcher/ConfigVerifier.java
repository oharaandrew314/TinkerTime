package io.andrewohara.tinkertime.launcher;

import io.andrewohara.tinkertime.models.ConfigFactory;
import io.andrewohara.tinkertime.views.InstallationSelector;

import com.google.inject.Inject;

class ConfigVerifier implements Runnable {

	private final ConfigFactory factory;
	private final InstallationSelector selector;

	@Inject
	ConfigVerifier(ConfigFactory factory, InstallationSelector selector){
		this.factory = factory;
		this.selector = selector;
	}

	@Override
	public void run() {
		if (factory.getConfig().getSelectedInstallation() == null){
			selector.toDialog();
		}
	}

}
