package aohara.tinkertime.controllers;

import aohara.tinkertime.models.ConfigFactory;
import aohara.tinkertime.views.InstallationSelector;

import com.google.inject.Inject;

public class ConfigVerifier {

	private final ConfigFactory factory;
	private final InstallationSelector selector;

	@Inject
	ConfigVerifier(ConfigFactory factory, InstallationSelector selector){
		this.factory = factory;
		this.selector = selector;
	}

	public void ensureValid(){
		if (factory.getConfig().getSelectedInstallation() == null){
			selector.toDialog();
		}
	}

}
