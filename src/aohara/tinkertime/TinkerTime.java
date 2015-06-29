package aohara.tinkertime;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import aohara.common.content.ImageManager;
import aohara.common.version.Version;
import aohara.tinkertime.controllers.ConfigVerifier;
import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.controllers.ModUpdateCoordinator;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.migration.Migrator;
import aohara.tinkertime.models.ConfigData;
import aohara.tinkertime.models.ConfigFactory;
import aohara.tinkertime.modules.MainModule;
import aohara.tinkertime.views.ModListCellRenderer;
import aohara.tinkertime.views.menus.MenuFactory;
import aohara.tinkertime.views.selector.ModSelectorPanelController;
import aohara.tinkertime.views.selector.ModSelectorPanelFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Main Class for Tinker Time
 *
 * @author Andrew O'Hara
 */
public class TinkerTime {

	public static final String
	NAME = "Tinker Time",
	AUTHOR = "oharaandrew314",
	DOWNLOAD_URL = "https://kerbalstuff.com/mod/243";
	public static final Version VERSION = Version.valueOf("1.4.4");
	public static final String
	SAFE_NAME = NAME.replace(" ", ""),
	FULL_NAME = String.format("%s v%s by %s", NAME, VERSION, AUTHOR);

	public static String getDbUrl(){
		Path path = Paths.get(System.getProperty("user.home"), "Documents", NAME, "TinkerTime-db");
		return String.format("jdbc:sqlite:file:%s", path.toString());
	}

	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new MainModule());

		// Perform Database Migrations
		injector.getInstance(Migrator.class).migrate();

		// Verify KSP Installation is valid, and ask user for one if not
		injector.getInstance(ConfigVerifier.class).ensureValid();

		// Set Listeners
		ModUpdateCoordinator modUpdateCoordinator = injector.getInstance(ModUpdateCoordinator.class);
		//modUpdateCoordinator.addHandler(injector.getInstance(ModMetaLoader.class));  //TODO Dao should listen for updates to mods
		ModSelectorPanelController selectorPanel = injector.getInstance(ModSelectorPanelFactory.class).create(new Dimension(800, 600), 0.35);
		modUpdateCoordinator.addHandler(selectorPanel);

		ModManager modManager = injector.getInstance(ModManager.class);
		modManager.reloadMods();
		modManager.addListener(injector.getInstance(ModListCellRenderer.class));  // TODO See if can be ported to ModUpdateCoordinator

		// Startup Tasks
		try {
			ConfigData config = injector.getInstance(ConfigFactory.class).getConfig();
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
				modManager.checkForModUpdates();
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.toString(), "Error Checking for Mod Updates", JOptionPane.ERROR_MESSAGE);
		}

		// Get App Icons
		ImageManager imageManager = new ImageManager();
		List<Image> appIcons = new ArrayList<Image>();
		appIcons.add(imageManager.getImage("icon/app/icon 128x128.png"));
		appIcons.add(imageManager.getImage("icon/app/icon 64x64.png"));
		appIcons.add(imageManager.getImage("icon/app/icon 32x32.png"));
		appIcons.add(imageManager.getImage("icon/app/icon 16x16.png"));

		// Initialize Frame
		MenuFactory menuFactory = injector.getInstance(MenuFactory.class);
		JFrame frame = new JFrame(TinkerTime.FULL_NAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setIconImages(appIcons);
		frame.setJMenuBar(menuFactory.createMenuBar());
		frame.add(menuFactory.createToolBar(), BorderLayout.NORTH);
		frame.add(selectorPanel.getComponent(), BorderLayout.CENTER);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
