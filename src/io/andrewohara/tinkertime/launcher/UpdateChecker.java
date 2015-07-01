package io.andrewohara.tinkertime.launcher;

import io.andrewohara.tinkertime.controllers.ModManager;
import io.andrewohara.tinkertime.db.ConfigFactory;
import io.andrewohara.tinkertime.io.crawlers.CrawlerFactory.UnsupportedHostException;
import io.andrewohara.tinkertime.models.ConfigData;

import java.net.MalformedURLException;

import javax.swing.JOptionPane;

import com.google.inject.Inject;

class UpdateChecker implements Runnable {

	private final ConfigFactory configFactory;
	private final ModManager modManager;

	@Inject
	UpdateChecker(ConfigFactory configFactory, ModManager modManager){
		this.configFactory = configFactory;
		this.modManager = modManager;
	}

	@Override
	public void run() {
		ConfigData config = configFactory.getConfig();
		// Check for App update on Startup
		if (config.isCheckForAppUpdatesOnStartup()){
			try {
				modManager.tryUpdateModManager();
			} catch (UnsupportedHostException | MalformedURLException e) {
				JOptionPane.showMessageDialog(null, e.toString(), "Error Checking for App Updates", JOptionPane.ERROR_MESSAGE);
			}
		}

		// Check for Mod Updates on Startup
		if (config.isCheckForModUpdatesOnStartup()){
			try {
				modManager.checkForModUpdates();
			} catch (UnsupportedHostException e) {
				JOptionPane.showMessageDialog(null, e.toString(), "Error Checking for Mod Updates", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

}
