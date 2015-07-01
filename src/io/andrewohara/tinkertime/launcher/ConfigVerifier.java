package io.andrewohara.tinkertime.launcher;

import io.andrewohara.tinkertime.db.ConfigFactory;
import io.andrewohara.tinkertime.views.InstallationSelectorView;

import com.google.inject.Inject;

class ConfigVerifier implements Runnable {

	private final ConfigFactory factory;
	private final InstallationSelectorView selector;

	@Inject
	ConfigVerifier(ConfigFactory factory, InstallationSelectorView selector){
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
