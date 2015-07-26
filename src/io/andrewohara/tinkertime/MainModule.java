package io.andrewohara.tinkertime;

import java.awt.Dimension;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.jsoup.nodes.Document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import io.andrewohara.common.OS;
import io.andrewohara.common.views.Dialogs;
import io.andrewohara.common.views.selectorPanel.SelectorPanelBuilder;
import io.andrewohara.tinkertime.controllers.ImportController;
import io.andrewohara.tinkertime.controllers.ModManager;
import io.andrewohara.tinkertime.db.DbConnectionString;
import io.andrewohara.tinkertime.io.crawlers.pageLoaders.JsonLoader;
import io.andrewohara.tinkertime.io.crawlers.pageLoaders.PageLoader;
import io.andrewohara.tinkertime.io.crawlers.pageLoaders.WebpageLoader;
import io.andrewohara.tinkertime.io.kspLauncher.GameExecStrategy;
import io.andrewohara.tinkertime.io.kspLauncher.GameLauncher;
import io.andrewohara.tinkertime.io.kspLauncher.LinuxExecStrategy;
import io.andrewohara.tinkertime.io.kspLauncher.OsxExecStrategy;
import io.andrewohara.tinkertime.io.kspLauncher.WindowsExecStrategy;
import io.andrewohara.tinkertime.models.ConfigData;
import io.andrewohara.tinkertime.models.Installation;
import io.andrewohara.tinkertime.models.ModFile;
import io.andrewohara.tinkertime.models.mod.Mod;
import io.andrewohara.tinkertime.views.Actions;
import io.andrewohara.tinkertime.views.InstallationSelectorView;
import io.andrewohara.tinkertime.views.SelectedInstallationView;
import io.andrewohara.tinkertime.views.modSelector.ModListCellRenderer;
import io.andrewohara.tinkertime.views.modSelector.ModListListener;
import io.andrewohara.tinkertime.views.modSelector.ModSelectorPanelController;
import io.andrewohara.tinkertime.views.modView.ModView;

public class MainModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(new TypeLiteral<PageLoader<Document>>(){}).to(WebpageLoader.class);
		bind(new TypeLiteral<PageLoader<JsonElement>>(){}).to(JsonLoader.class);
	}

	@Provides
	GameExecStrategy getGameExecStrategy(){
		switch(OS.getOs()){
		case Windows: return new WindowsExecStrategy();
		case Linux: return new LinuxExecStrategy();
		case Osx: return new OsxExecStrategy();
		default: throw new IllegalStateException();
		}
	}

	@Provides
	Gson provideGson(){
		return new GsonBuilder().setPrettyPrinting().create();
	}

	@Provides
	Executor provideExecutor(){
		return Executors.newSingleThreadExecutor();
	}

	@Provides
	ThreadPoolExecutor provideThreadedExecutor(){
		return (ThreadPoolExecutor) Executors.newFixedThreadPool(ConfigData.NUM_CONCURRENT_DOWNLOADS);
	}

	@Singleton
	@Provides
	ConnectionSource getConnectionSource(DbConnectionString connectionString) throws SQLException {
		return new JdbcConnectionSource(connectionString.getUrl());
	}

	@Provides
	Dao<Mod, Integer> getModsDao(ConnectionSource source) throws SQLException{
		return DaoManager.createDao(source, Mod.class);
	}

	@Provides
	Dao<Installation, Integer> getInstallationsDao(ConnectionSource source) throws SQLException{
		return DaoManager.createDao(source, Installation.class);
	}

	@Provides
	Dao<ConfigData, Integer> getConfigDao(ConnectionSource source) throws SQLException{
		return DaoManager.createDao(source, ConfigData.class);
	}

	@Provides
	Dao<ModFile, Integer> getModFilesDao(ConnectionSource source) throws SQLException{
		return DaoManager.createDao(source, ModFile.class);
	}

	@Singleton
	@Provides
	ConfigData getConfigData(Dao<ConfigData, Integer> configDao) throws SQLException{
		List<ConfigData> configs = configDao.queryForAll();
		return !configs.isEmpty() ? configs.get(0) : new ConfigData(configDao);
	}

	@Singleton
	@Provides
	DbConnectionString getDbConnectionString(){
		return new DbConnectionString(){
			@Override
			public String getUrl() {
				Path path = TinkerTimeLauncher.getHomePath().resolve("TinkerTime-db");
				return String.format("jdbc:h2:file:%s", path.toString());
			}

		};
	}

	@Singleton
	@Provides
	Dialogs getDialogs(){
		return new Dialogs(TinkerTimeLauncher.getHomePath().resolve("Exceptions"));
	}

	////////////////////
	// Swing Elements //
	////////////////////

	@Provides
	public JToolBar createToolBar(ModManager mm, GameLauncher gameLauncher, ConfigData config, InstallationSelectorView installationSelector, SelectedInstallationView installationView, Dialogs dialogs){
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);

		toolBar.add(new Actions.LaunchKspAction(toolBar, mm, gameLauncher, dialogs)).setFocusPainted(false);
		toolBar.addSeparator();

		toolBar.add(new Actions.OpenGameDataFolder(toolBar, mm, config, dialogs)).setFocusPainted(false);
		toolBar.add(new Actions.LaunchInstallationSelector(toolBar, mm, installationSelector, dialogs)).setFocusPainted(false);

		toolBar.addSeparator();

		toolBar.add(new Actions.AddModAction(toolBar, mm, dialogs)).setFocusPainted(false);
		toolBar.add(new Actions.AddModZip(toolBar, mm, dialogs)).setFocusPainted(false);

		toolBar.addSeparator();

		toolBar.add(new Actions.UpdateModAction(toolBar, mm, dialogs)).setFocusPainted(false);
		toolBar.add(new Actions.CheckforUpdatesAction(toolBar, mm, dialogs)).setFocusPainted(false);

		toolBar.addSeparator();

		toolBar.add(installationView.getComponent());

		return toolBar;
	}

	@Provides
	public JMenuBar createMenuBar(ModManager mm, ImportController importController, ConfigData config, Dialogs dialogs){
		JMenuBar menuBar = new JMenuBar();

		JMenu modMenu = new JMenu("Mod");
		modMenu.add(new Actions.EnableDisableModAction(menuBar, mm, dialogs).withoutIcon());
		modMenu.add(new Actions.UpdateModAction(menuBar, mm, dialogs).withoutIcon());
		modMenu.add(new Actions.DeleteModAction(menuBar, mm, dialogs).withoutIcon());
		menuBar.add(modMenu);

		JMenu updateMenu = new JMenu("Updates");
		updateMenu.add(new Actions.UpdateAllAction(menuBar, mm, dialogs).withoutIcon());
		updateMenu.add(new Actions.CheckforUpdatesAction(menuBar, mm, dialogs).withoutIcon());
		updateMenu.add(new Actions.UpdateTinkerTime(menuBar, mm, dialogs).withoutIcon());
		menuBar.add(updateMenu);

		JMenu importExportMenu = new JMenu("Import/Export");
		importExportMenu.add(new Actions.ImportMods(menuBar, importController, dialogs).withoutIcon());
		importExportMenu.add(new Actions.ExportMods(menuBar, importController, dialogs).withoutIcon());
		menuBar.add(importExportMenu);

		JMenu optionsMenu = new JMenu("Options");
		optionsMenu.setHorizontalAlignment(SwingConstants.LEFT);

		optionsMenu.add(new Actions.UpdateLaunchArgsAction(config, dialogs));

		// Check for app updates on startup option
		JCheckBox checkAppUpdatesBox = new JCheckBox(
				String.format("Check for %s Updates on Startup", TinkerTimeLauncher.NAME),
				config.isCheckForAppUpdatesOnStartup()
				);
		checkAppUpdatesBox.addActionListener(new Actions.CheckForAppUpdatesAction(checkAppUpdatesBox, config, dialogs));
		optionsMenu.add(checkAppUpdatesBox);

		// check for mod updates on startup option
		JCheckBox checkModUpdatesBox = new JCheckBox("Check for Mod Updates on Startup", config.isCheckForModUpdatesOnStartup());
		checkModUpdatesBox.addActionListener(new Actions.CheckForModUpdatesAction(checkModUpdatesBox, config, dialogs));
		optionsMenu.add(checkModUpdatesBox);

		menuBar.add(optionsMenu);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(new Actions.ContactAction(menuBar, mm, dialogs).withoutIcon());
		helpMenu.add(Actions.newWebsiteAction(menuBar, dialogs));
		helpMenu.add(Actions.newHelpAction(menuBar, dialogs).withoutIcon());
		helpMenu.add(new Actions.AboutAction(menuBar, mm, dialogs).withoutIcon());
		menuBar.add(helpMenu);

		return menuBar;
	}

	@Provides
	public JPopupMenu createPopupMenu(ModManager mm, Dialogs dialogs){
		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.add(new Actions.EnableDisableModAction(popupMenu, mm, dialogs));
		popupMenu.add(new Actions.UpdateModAction(popupMenu, mm, dialogs));
		popupMenu.add(new Actions.DeleteModAction(popupMenu, mm, dialogs));
		return popupMenu;
	}

	@Singleton
	@Provides
	public ModSelectorPanelController getModSelector(ModListCellRenderer renderer, ModListListener listListener, JPopupMenu popupMenu, ModManager mm, ModView modView, Dialogs dialogs){
		SelectorPanelBuilder<Mod> spBuilder = new SelectorPanelBuilder<>(new Dimension(800, 600), 0.35);
		spBuilder.setListCellRenderer(renderer);
		spBuilder.setContextMenu(popupMenu);

		spBuilder.addKeyListener(listListener);
		spBuilder.addSelectionListener(listListener);

		return new ModSelectorPanelController(spBuilder.createSelectorPanel(modView), mm, dialogs);
	}
}
