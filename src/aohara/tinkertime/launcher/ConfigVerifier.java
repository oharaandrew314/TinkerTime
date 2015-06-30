package aohara.tinkertime.launcher;

import aohara.tinkertime.models.ConfigFactory;
import aohara.tinkertime.views.InstallationSelector;

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
