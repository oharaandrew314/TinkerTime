package io.andrewohara.tinkertime;

import io.andrewohara.common.OS;
import io.andrewohara.tinkertime.controllers.ImportController;
import io.andrewohara.tinkertime.controllers.ModManager;
import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateCoordinator;
import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateCoordinatorImpl;
import io.andrewohara.tinkertime.db.ConfigFactory;
import io.andrewohara.tinkertime.db.DaoConfigFactory;
import io.andrewohara.tinkertime.db.DaoInstallationManager;
import io.andrewohara.tinkertime.db.DaoModLoader;
import io.andrewohara.tinkertime.db.DbConnectionString;
import io.andrewohara.tinkertime.db.DbConnectionStringImpl;
import io.andrewohara.tinkertime.db.InstallationManager;
import io.andrewohara.tinkertime.db.ModLoader;
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
import io.andrewohara.tinkertime.models.ModImage;
import io.andrewohara.tinkertime.models.Readme;
import io.andrewohara.tinkertime.models.mod.Mod;
import io.andrewohara.tinkertime.views.Actions;
import io.andrewohara.tinkertime.views.InstallationSelectorView;

import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

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
import com.j256.ormlite.table.TableUtils;

public class MainModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(new TypeLiteral<PageLoader<Document>>(){}).to(WebpageLoader.class);
		bind(new TypeLiteral<PageLoader<JsonElement>>(){}).to(JsonLoader.class);
		bind(ConfigFactory.class).to(DaoConfigFactory.class);
		bind(ModLoader.class).to(DaoModLoader.class);
		bind(ModUpdateCoordinator.class).to(ModUpdateCoordinatorImpl.class);
		bind(InstallationManager.class).to(DaoInstallationManager.class);
		bind(DbConnectionString.class).to(DbConnectionStringImpl.class);
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
		return (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
	}

	@Singleton
	@Provides
	ConnectionSource getConnectionSource(DbConnectionString connectionString) throws SQLException {
		return new JdbcConnectionSource(connectionString.getUrl());
	}

	@Provides
	Dao<Mod, Integer> getModsDao(ConnectionSource source) throws SQLException{
		TableUtils.createTableIfNotExists(source, Mod.class);  //TODO Remove when migration created
		return DaoManager.createDao(source, Mod.class);
	}

	@Provides
	Dao<Installation, Integer> getInstallationsDao(ConnectionSource source) throws SQLException{
		TableUtils.createTableIfNotExists(source, Installation.class);  //TODO Remove when migration created
		return DaoManager.createDao(source, Installation.class);
	}

	@Provides
	Dao<ConfigData, Integer> getConfigDao(ConnectionSource source) throws SQLException{
		TableUtils.createTableIfNotExists(source, ConfigData.class);  //TODO Remove when migration created
		return DaoManager.createDao(source, ConfigData.class);
	}

	@Provides
	Dao<ModFile, Integer> getModFilesDao(ConnectionSource source) throws SQLException{
		TableUtils.createTableIfNotExists(source, ModFile.class);  //TODO Remove when migration created
		return DaoManager.createDao(source, ModFile.class);
	}

	@Provides
	Dao<ModImage, Integer> getModImageDao(ConnectionSource source) throws SQLException{
		TableUtils.createTableIfNotExists(source, ModImage.class);  //TODO Remove when migration created
		return DaoManager.createDao(source, ModImage.class);
	}

	@Provides
	Dao<Readme, Integer> getReadmesDao(ConnectionSource source) throws SQLException{
		TableUtils.createTableIfNotExists(source, Readme.class);  //TODO Remove when migration created
		return DaoManager.createDao(source, Readme.class);
	}

	@Provides
	public JToolBar createToolBar(ModManager mm, GameLauncher gameLauncher, ConfigFactory configFactory, InstallationSelectorView installationSelector){
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);

		toolBar.add(new Actions.LaunchKspAction(toolBar, mm, gameLauncher)).setFocusPainted(false);
		toolBar.addSeparator();

		toolBar.add(new Actions.OpenGameDataFolder(toolBar, mm, configFactory)).setFocusPainted(false);
		toolBar.add(new Actions.LaunchInstallationSelector(toolBar, mm, installationSelector)).setFocusPainted(false);
		//toolBar.add(new Actions.OptionsAction(toolBar, configController)).setFocusPainted(false); FIXME reimplement options toolbar button

		toolBar.addSeparator();

		toolBar.add(new Actions.AddModAction(toolBar, mm)).setFocusPainted(false);
		toolBar.add(new Actions.AddModZip(toolBar, mm)).setFocusPainted(false);

		toolBar.addSeparator();

		toolBar.add(new Actions.UpdateModAction(toolBar, mm)).setFocusPainted(false);
		toolBar.add(new Actions.CheckforUpdatesAction(toolBar, mm)).setFocusPainted(false);

		return toolBar;
	}

	@Provides
	public JMenuBar createMenuBar(ModManager mm, ImportController importController){
		JMenuBar menuBar = new JMenuBar();

		JMenu modMenu = new JMenu("Mod");
		modMenu.add(new Actions.EnableDisableModAction(menuBar, mm).withoutIcon());
		modMenu.add(new Actions.UpdateModAction(menuBar, mm).withoutIcon());
		modMenu.add(new Actions.DeleteModAction(menuBar, mm).withoutIcon());
		menuBar.add(modMenu);

		JMenu updateMenu = new JMenu("Updates");
		updateMenu.add(new Actions.UpdateAllAction(menuBar, mm).withoutIcon());
		updateMenu.add(new Actions.CheckforUpdatesAction(menuBar, mm).withoutIcon());
		updateMenu.add(new Actions.UpdateTinkerTime(menuBar, mm).withoutIcon());
		menuBar.add(updateMenu);

		JMenu importExportMenu = new JMenu("Import/Export");
		importExportMenu.add(new Actions.ImportMods(menuBar, importController).withoutIcon());
		importExportMenu.add(new Actions.ExportMods(menuBar, importController).withoutIcon());
		menuBar.add(importExportMenu);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(new Actions.AboutAction(menuBar, mm).withoutIcon());
		helpMenu.add(Actions.newHelpAction(menuBar).withoutIcon());
		helpMenu.add(new Actions.ContactAction(menuBar, mm).withoutIcon());
		menuBar.add(helpMenu);

		return menuBar;
	}

	@Provides
	public JPopupMenu createPopupMenu(ModManager mm){
		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.add(new Actions.EnableDisableModAction(popupMenu, mm));
		popupMenu.add(new Actions.UpdateModAction(popupMenu, mm));
		popupMenu.add(new Actions.DeleteModAction(popupMenu, mm));
		return popupMenu;
	}
}
