package aohara.tinkertime.launcher;

import java.net.MalformedURLException;

import javax.swing.JOptionPane;

import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.io.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.models.ConfigData;
import aohara.tinkertime.models.ConfigFactory;

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
