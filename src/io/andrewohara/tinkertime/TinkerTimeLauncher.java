package io.andrewohara.tinkertime;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.SplashScreen;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import io.andrewohara.common.content.ImageManager;
import io.andrewohara.common.version.Version;
import io.andrewohara.common.views.Dialogs;
import io.andrewohara.tinkertime.controllers.ModManager;
import io.andrewohara.tinkertime.controllers.ModUpdateCoordinator;
import io.andrewohara.tinkertime.db.DbConnectionString;
import io.andrewohara.tinkertime.io.crawlers.CrawlerFactory.UnsupportedHostException;
import io.andrewohara.tinkertime.models.ConfigData;
import io.andrewohara.tinkertime.views.InstallationSelectorView;
import io.andrewohara.tinkertime.views.SelectedInstallationView;
import io.andrewohara.tinkertime.views.modSelector.ModListCellRenderer;
import io.andrewohara.tinkertime.views.modSelector.ModSelectorPanelController;

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
	public static final Version VERSION = Version.valueOf("2.0.0");
	public static final String
	SAFE_NAME = NAME.replace(" ", ""),
	FULL_NAME = String.format("%s %s by %s", NAME, VERSION, AUTHOR);

	private final Collection<Runnable> startupTasks = new LinkedList<>();

	@Inject
	TinkerTimeLauncher(ConfigVerifier configVerifier, SetupListeners setupListeners, LoadModsTask startupModLoader, UpdateChecker updateChecker, MainFrameLauncher mainFrameLauncher){
		startupTasks.add(setupListeners);
		startupTasks.add(configVerifier);
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

	public static Path getHomePath(){
		return Paths.get(System.getProperty("user.home"), "Documents", TinkerTimeLauncher.NAME);
	}

	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new MainModule());

		// Migrate database
		injector.getInstance(DatabaseMigrator.class).run();

		// Launch Application
		TinkerTimeLauncher launcher = injector.getInstance(TinkerTimeLauncher.class);
		launcher.run();
	}

	///////////////////
	// Startup Tasks //
	///////////////////

	public static class DatabaseMigrator implements Runnable {

		private final DbConnectionString connectionString;

		@Inject
		public DatabaseMigrator(DbConnectionString connectionString){
			this.connectionString = connectionString;
		}

		@Override
		public void run() {
			// Perform Database Migration
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

		private final ConfigData config;
		private final InstallationSelectorView selector;

		@Inject
		ConfigVerifier(ConfigData config, InstallationSelectorView selector){
			this.config = config;
			this.selector = selector;
		}

		@Override
		public void run() {
			if (config.getSelectedInstallation() == null){
				selector.toDialog();
			}
		}
	}

	private static class SetupListeners implements Runnable {

		private final ModSelectorPanelController modSelectorPanelController;
		private final ModListCellRenderer modListCellRenderer;
		private final ModUpdateCoordinator modUpdateCoordinator;
		private final SelectedInstallationView installationView;

		@Inject
		SetupListeners(ModUpdateCoordinator modUpdateCoordinator, ModSelectorPanelController modSelectorPanelController, ModListCellRenderer modListCellRender, SelectedInstallationView installationView) {
			this.modUpdateCoordinator = modUpdateCoordinator;
			this.modSelectorPanelController = modSelectorPanelController;
			this.modListCellRenderer = modListCellRender;
			this.installationView = installationView;
		}

		@Override
		public void run() {
			modUpdateCoordinator.setListeners(modSelectorPanelController, modListCellRenderer, installationView);
		}
	}

	private static class LoadModsTask implements Runnable {

		private final ModUpdateCoordinator updateCooridnator;
		private final ConfigData config;

		@Inject
		LoadModsTask(ModUpdateCoordinator updateCooridnator, ConfigData config){
			this.updateCooridnator = updateCooridnator;
			this.config = config;
		}

		@Override
		public void run() {
			updateCooridnator.changeInstallation(config.getSelectedInstallation());
		}

	}

	private static class UpdateChecker implements Runnable {

		private final ConfigData config;
		private final ModManager modManager;
		private final Dialogs dialogs;

		@Inject
		UpdateChecker(ConfigData config, ModManager modManager, Dialogs dialogs){
			this.config = config;
			this.modManager = modManager;
			this.dialogs = dialogs;
		}

		@Override
		public void run() {
			// Check for App update on Startup
			if (config.isCheckForAppUpdatesOnStartup()){
				try {
					modManager.tryUpdateModManager();
				} catch (UnsupportedHostException | MalformedURLException | SQLException e) {
					dialogs.errorDialog(null, "Error Checking for App Updates", e);
				}
			}

			// Check for Mod Updates on Startup
			if (config.isCheckForModUpdatesOnStartup()){
				try {
					modManager.checkForModUpdates();
				} catch (UnsupportedHostException e) {
					dialogs.errorDialog(null, "Error Checking for Mod Updates", e);
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

			// Hide Splash Screen so the JFrame does not hide when appearing
			SplashScreen s = SplashScreen.getSplashScreen();
			if (s != null){
				s.close();
			}

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
			frame.toFront();
		}
	}
}
