package io.andrewohara.tinkertime.launcher;

import io.andrewohara.common.OS;
import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateCoordinator;
import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateCoordinatorImpl;
import io.andrewohara.tinkertime.db.ConfigFactory;
import io.andrewohara.tinkertime.db.DaoConfigFactory;
import io.andrewohara.tinkertime.db.DaoInstallationManager;
import io.andrewohara.tinkertime.db.DaoModLoader;
import io.andrewohara.tinkertime.db.InstallationManager;
import io.andrewohara.tinkertime.db.ModLoader;
import io.andrewohara.tinkertime.io.crawlers.pageLoaders.JsonLoader;
import io.andrewohara.tinkertime.io.crawlers.pageLoaders.PageLoader;
import io.andrewohara.tinkertime.io.crawlers.pageLoaders.WebpageLoader;
import io.andrewohara.tinkertime.io.kspLauncher.GameExecStrategy;
import io.andrewohara.tinkertime.io.kspLauncher.LinuxExecStrategy;
import io.andrewohara.tinkertime.io.kspLauncher.OsxExecStrategy;
import io.andrewohara.tinkertime.io.kspLauncher.WindowsExecStrategy;
import io.andrewohara.tinkertime.models.ConfigData;
import io.andrewohara.tinkertime.models.Installation;
import io.andrewohara.tinkertime.models.ModFile;
import io.andrewohara.tinkertime.models.ModImage;
import io.andrewohara.tinkertime.models.Readme;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.jsoup.nodes.Document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class MainModule extends AbstractModule {

	private ConnectionSource dbConnection;

	@Override
	protected void configure() {
		bind(new TypeLiteral<PageLoader<Document>>(){}).to(WebpageLoader.class);
		bind(new TypeLiteral<PageLoader<JsonElement>>(){}).to(JsonLoader.class);
		bind(GameExecStrategy.class).to(getExecStrategyType());
		bind(ConfigFactory.class).to(DaoConfigFactory.class);
		bind(ModLoader.class).to(DaoModLoader.class);
		bind(ModUpdateCoordinator.class).to(ModUpdateCoordinatorImpl.class);
		bind(InstallationManager.class).to(DaoInstallationManager.class);
	}

	private Class<? extends GameExecStrategy> getExecStrategyType(){
		switch(OS.getOs()){
		case Windows: return WindowsExecStrategy.class;
		case Linux: return LinuxExecStrategy.class;
		case Osx: return OsxExecStrategy.class;
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

	@Provides
	ConnectionSource getConnectionSource(){
		try {
			if (dbConnection == null){
				dbConnection = new JdbcConnectionSource(TinkerTimeLauncher.getDbUrl());
			}
			return dbConnection;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Provides
	Dao<Mod, Integer> getModsDao(){
		try {
			//return DaoManager.createDao(getConnectionSource();, Mod.class);
			ConnectionSource connection = getConnectionSource();
			TableUtils.createTableIfNotExists(connection, Mod.class);  //TODO Remove when migration created
			return DaoManager.createDao(connection, Mod.class);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Provides
	Dao<Installation, Integer> getInstallationsDao(){
		try {
			//return DaoManager.createDao(getConnectionSource(), Installation.class);
			ConnectionSource source = getConnectionSource();
			TableUtils.createTableIfNotExists(source, Installation.class);  //TODO Remove when migration created
			return DaoManager.createDao(source, Installation.class);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Provides
	Dao<ConfigData, Integer> getConfigDao(){
		try {
			//return DaoManager.createDao(getConnectionSource(), ConfigData.class);
			ConnectionSource source = getConnectionSource();
			TableUtils.createTableIfNotExists(source, ConfigData.class);  //TODO Remove when migration created
			return DaoManager.createDao(source, ConfigData.class);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Provides
	Dao<ModFile, Integer> getModFilesDao(){
		try {
			ConnectionSource source = getConnectionSource();
			TableUtils.createTableIfNotExists(source, ModFile.class);  //TODO Remove when migration created
			return DaoManager.createDao(source, ModFile.class);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Provides
	Dao<ModImage, Integer> getModImageDao(){
		try {
			ConnectionSource source = getConnectionSource();
			TableUtils.createTableIfNotExists(source, ModImage.class);  //TODO Remove when migration created
			return DaoManager.createDao(source, ModImage.class);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Provides
	Dao<Readme, Integer> getReadmesDao(){
		try {
			ConnectionSource source = getConnectionSource();
			TableUtils.createTableIfNotExists(source, Readme.class);  //TODO Remove when migration created
			return DaoManager.createDao(source, Readme.class);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
