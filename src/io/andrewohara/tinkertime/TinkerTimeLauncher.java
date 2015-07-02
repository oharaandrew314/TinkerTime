package io.andrewohara.tinkertime;

import io.andrewohara.common.content.ImageManager;
import io.andrewohara.common.version.Version;
import io.andrewohara.tinkertime.controllers.ModManager;
import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateCoordinator;
import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateCoordinatorImpl;
import io.andrewohara.tinkertime.db.ConfigFactory;
import io.andrewohara.tinkertime.io.crawlers.CrawlerFactory.UnsupportedHostException;
import io.andrewohara.tinkertime.models.ConfigData;
import io.andrewohara.tinkertime.views.InstallationSelectorView;
import io.andrewohara.tinkertime.views.menus.MenuFactory;
import io.andrewohara.tinkertime.views.modSelector.ModListCellRenderer;
import io.andrewohara.tinkertime.views.modSelector.ModSelectorPanelFactory;

import java.awt.BorderLayout;
import java.awt.Image;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.flywaydb.core.Flyway;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Main Class for Tinker Time
 *
 * @author Andrew O'Hara
 */
public class TinkerTimeLauncher implements Runnable {

	public static final String
	NAME = "Tinker Time",
	AUTHOR = "oharaandrew314",
	DOWNLOAD_URL = "https://kerbalstuff.com/mod/243";
	public static final Version VERSION = Version.valueOf("1.4.5");
	public static final String
	SAFE_NAME = NAME.replace(" ", ""),
	FULL_NAME = String.format("%s v%s by %s", NAME, VERSION, AUTHOR);

	public static String getDbUrl(){
		Path path = Paths.get(System.getProperty("user.home"), "Documents", NAME, "TinkerTime-db");
		return String.format("jdbc:h2:file:%s", path.toString());
	}

	private final Collection<Runnable> startupTasks = new LinkedList<>();

	@Inject
	TinkerTimeLauncher(DatabaseMigrator migrator, ConfigVerifier configVerifier, SetupListeners setupListeners, LoadModsTask startupModLoader, UpdateChecker updateChecker, MainFrameLauncher mainFrameLauncher){
		//startupTasks.add(migrator);  FIXME re-enable migrator later
		startupTasks.add(configVerifier);
		startupTasks.add(setupListeners);
		startupTasks.add(startupModLoader);
		startupTasks.add(updateChecker);
		startupTasks.add(mainFrameLauncher);
	}

	@Override
	public void run() {
		for (Runnable task : startupTasks){
			task.run();
		}
	}

	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new MainModule());
		TinkerTimeLauncher launcher = injector.getInstance(TinkerTimeLauncher.class);
		launcher.run();
	}

	///////////////////
	// Startup Tasks //
	///////////////////

	private static class DatabaseMigrator implements Runnable {

		@Override
		public void run() {
			Flyway flyway = new Flyway();
			flyway.setBaselineOnMigrate(true);
			flyway.setLocations("io/andrewohara/tinkertime/db/migration");
			flyway.setDataSource(TinkerTimeLauncher.getDbUrl(), null, null);  // TODO Inject URL
			flyway.migrate();
		}
	}

	private static class ConfigVerifier implements Runnable {

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

	private static class SetupListeners implements Runnable {

		private final ModUpdateCoordinator modUpdateCoordinator;
		private final ModSelectorPanelFactory modSelectorPanelFactory;
		private final ModListCellRenderer modListCellRenderer;

		@Inject
		SetupListeners(ModUpdateCoordinator modUpdateCoordinator, ModSelectorPanelFactory modSelectorPanelFactory, ModListCellRenderer modListCellRender) {
			this.modUpdateCoordinator = modUpdateCoordinator;

			this.modSelectorPanelFactory = modSelectorPanelFactory;
			this.modListCellRenderer = modListCellRender;
		}

		@Override
		public void run() {
			modUpdateCoordinator.setListeners(modSelectorPanelFactory, modListCellRenderer);
		}
	}

	private static class LoadModsTask implements Runnable {

		private final ModUpdateCoordinatorImpl updateCooridnator;
		private final ConfigFactory configFactory;

		@Inject
		LoadModsTask(ModUpdateCoordinatorImpl updateCooridnator, ConfigFactory configFactory){
			this.updateCooridnator = updateCooridnator;
			this.configFactory = configFactory;
		}

		@Override
		public void run() {
			updateCooridnator.reload(configFactory.getConfig().getSelectedInstallation());
		}

	}

	private static class UpdateChecker implements Runnable {

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

	private static class MainFrameLauncher implements Runnable {

		private final MenuFactory menuFactory;
		private final ModSelectorPanelFactory selectorFactory;

		@Inject
		MainFrameLauncher(MenuFactory menuFactory, ModSelectorPanelFactory selectorFactory){
			this.menuFactory = menuFactory;
			this.selectorFactory = selectorFactory;
		}

		@Override
		public void run() {
			// Get App Icons
			ImageManager imageManager = new ImageManager();
			List<Image> appIcons = new ArrayList<Image>();
			appIcons.add(imageManager.getImage("icon/app/icon 128x128.png"));
			appIcons.add(imageManager.getImage("icon/app/icon 64x64.png"));
			appIcons.add(imageManager.getImage("icon/app/icon 32x32.png"));
			appIcons.add(imageManager.getImage("icon/app/icon 16x16.png"));

			// Initialize Frame
			JFrame frame = new JFrame(TinkerTimeLauncher.FULL_NAME);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new BorderLayout());
			frame.setIconImages(appIcons);
			frame.setJMenuBar(menuFactory.createMenuBar());
			frame.add(menuFactory.createToolBar(), BorderLayout.NORTH);
			frame.add(selectorFactory.get().getComponent(), BorderLayout.CENTER);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);

		}
	}


}
