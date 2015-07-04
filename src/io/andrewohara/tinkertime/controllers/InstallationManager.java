package io.andrewohara.tinkertime.controllers;

import io.andrewohara.tinkertime.models.ConfigData;
import io.andrewohara.tinkertime.models.Installation;
import io.andrewohara.tinkertime.models.Installation.InvalidGameDataPathException;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Collection;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;

public class InstallationManager {

	private final ConfigData config;
	private final Dao<Installation, Integer> installationsDao;
	private final ModUpdateCoordinator modUpdateCoordinator;
	private final ModLoader modLoader;

	@Inject
	InstallationManager(ConfigData config, Dao<Installation, Integer> installationsDao, ModUpdateCoordinator modUpdateCoordinator, ModLoader modLoader){
		this.config = config;
		this.installationsDao = installationsDao;
		this.modUpdateCoordinator = modUpdateCoordinator;
		this.modLoader = modLoader;
	}

	public Installation newInstallation(String name, Path folder) throws InvalidGameDataPathException, SQLException{
		Installation installation = new Installation(name, folder, installationsDao);
		modLoader.createDefaultsFor(installation);
		return installation;
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
			modUpdateCoordinator.changeInstallation(installation);
		} catch (SQLException e){

		}
	}

	public Installation getSelectedInstallation(){
		return config.getSelectedInstallation();
	}

}
