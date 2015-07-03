package io.andrewohara.tinkertime;

import io.andrewohara.common.content.ImageManager;
import io.andrewohara.common.version.Version;
import io.andrewohara.tinkertime.controllers.ModManager;
import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateCoordinator;
import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateCoordinatorImpl;
import io.andrewohara.tinkertime.db.ConfigFactory;
import io.andrewohara.tinkertime.db.DbConnectionString;
import io.andrewohara.tinkertime.io.crawlers.CrawlerFactory.UnsupportedHostException;
import io.andrewohara.tinkertime.models.ConfigData;
import io.andrewohara.tinkertime.views.InstallationSelectorView;
import io.andrewohara.tinkertime.views.modSelector.ModListCellRenderer;
import io.andrewohara.tinkertime.views.modSelector.ModSelectorPanelController;

import java.awt.BorderLayout;
import java.awt.Image;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

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

	private final Collection<Runnable> startupTasks = new LinkedList<>();

	@Inject
	TinkerTimeLauncher(DatabaseMigrator migrator, ConfigVerifier configVerifier, SetupListeners setupListeners, LoadModsTask startupModLoader, UpdateChecker updateChecker, MainFrameLauncher mainFrameLauncher){
		//startupTasks.add(migrator);
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

		private final DbConnectionString connectionString;

		@Inject
		DatabaseMigrator(DbConnectionString connectionString){
			this.connectionString = connectionString;
		}

		@Override
		public void run() {
			Flyway flyway = new Flyway();
			flyway.setBaselineOnMigrate(true);
			flyway.setLocations("io/andrewohara/tinkertime/db/migration");
			flyway.setDataSource(connectionString.getUrl(), null, null);

			try {
				flyway.migrate();
			} catch (FlywayException e){
				flyway.repair();
				throw e;
			}
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

		private final ModSelectorPanelController modSelectorPanelController;
		private final ModListCellRenderer modListCellRenderer;
		private final ModUpdateCoordinator modUpdateCoordinator;

		@Inject
		SetupListeners(ModUpdateCoordinator modUpdateCoordinator, ModSelectorPanelController modSelectorPanelController, ModListCellRenderer modListCellRender) {
			this.modUpdateCoordinator = modUpdateCoordinator;
			this.modSelectorPanelController = modSelectorPanelController;
			this.modListCellRenderer = modListCellRender;
		}

		@Override
		public void run() {
			modUpdateCoordinator.setListeners(modSelectorPanelController, modListCellRenderer);
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

		private final ModSelectorPanelController modSelectorPanelController;
		private final JMenuBar menuBar;
		private final JToolBar toolBar;

		@Inject
		MainFrameLauncher(ModSelectorPanelController modSelectorPanelController, JMenuBar menuBar, JToolBar toolBar){
			this.modSelectorPanelController = modSelectorPanelController;
			this.menuBar = menuBar;
			this.toolBar = toolBar;
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
			frame.setJMenuBar(menuBar);
			frame.add(toolBar, BorderLayout.NORTH);
			frame.add(modSelectorPanelController.getComponent(), BorderLayout.CENTER);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);

		}
	}


}
