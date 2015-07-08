package io.andrewohara.tinkertime.controllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;

import io.andrewohara.tinkertime.controllers.ModManager.ModUpdateFailedException;
import io.andrewohara.tinkertime.io.crawlers.CrawlerFactory;
import io.andrewohara.tinkertime.models.ConfigData;
import io.andrewohara.tinkertime.models.Installation;
import io.andrewohara.tinkertime.models.Installation.InvalidGameDataPathException;

public class InstallationManager {

	private final ConfigData config;
	private final Dao<Installation, Integer> installationsDao;
	private final ModUpdateCoordinator modUpdateCoordinator;
	private final ModManager modManager;

	@Inject
	InstallationManager(ConfigData config, Dao<Installation, Integer> installationsDao, ModUpdateCoordinator modUpdateCoordinator, ModManager modManager){
		this.config = config;
		this.installationsDao = installationsDao;
		this.modUpdateCoordinator = modUpdateCoordinator;
		this.modManager = modManager;
	}

	private Collection<URL> getDefaultModUrls(){
		try {
			return Collections.singleton(new URL("https", CrawlerFactory.HOST_MODULE_MANAGER, "/jenkins/job/ModuleManager"));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);  // You messed up
		}
	}

	public Installation newInstallation(String name, Path folder) throws InvalidGameDataPathException, SQLException{
		return new Installation(name, folder, installationsDao);
	}

	public Collection<Installation> getInstallations() {
		try {
			return installationsDao.queryForAll();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void changeInstallation(Installation installation) {
		try {
			config.setSelectedInstallation(installation);

			// If installation has no mods, download default ones
			if (installation.getMods().isEmpty()){
				for (URL newModUrl : getDefaultModUrls()){
					try {
						modManager.downloadNewMod(newModUrl);
					} catch (ModUpdateFailedException e) {
						e.printStackTrace();
					}
				}
			}

			modUpdateCoordinator.changeInstallation(installation);
		} catch (SQLException e){

		}
	}

	public Installation getSelectedInstallation(){
		return config.getSelectedInstallation();
	}

}
