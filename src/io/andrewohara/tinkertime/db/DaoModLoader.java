package io.andrewohara.tinkertime.db;

import io.andrewohara.tinkertime.models.ConfigData;
import io.andrewohara.tinkertime.models.Installation;
import io.andrewohara.tinkertime.models.ModFile;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;

import org.flywaydb.core.internal.util.ObjectUtils;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;

public class DaoModLoader implements ModLoader {

	private final Dao<Mod, Integer> modDao;
	private final Dao<ModFile, Integer> modFilesDao;
	private final Dao<Installation, Integer> installationDao;
	private final ConfigFactory configFactory;

	@Inject
	DaoModLoader(Dao<Mod, Integer> modDao, Dao<ModFile, Integer> modFilesDao, Dao<Installation, Integer> installationDao, ConfigFactory configFactory){
		this.modDao = modDao;
		this.modFilesDao = modFilesDao;
		this.installationDao = installationDao;
		this.configFactory = configFactory;
	}

	@Override
	public Mod get(int id){
		try {
			return modDao.queryForId(id);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void updateMod(Mod mod) {
		try {
			// Add mod to installation if it is not already there
			Installation installation = mod.getInstallation();
			if (installation.addMod(mod)){
				installationDao.update(installation);
			}

			// Create Mod
			modDao.createOrUpdate(mod);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void deleteMod(Mod mod) {
		try {
			for (ModFile modFile : mod.getModFiles()){
				modFilesDao.delete(modFile);
			}
			modDao.delete(mod);
			Installation installation = getInstallation();
			installation.unlinkMod(mod);
			installationDao.update(installation);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void reload(Installation installation) {
		ConfigData config = configFactory.getConfig();
		config.setSelectedInstallation(installation);
		configFactory.update(config);
	}

	@Override
	public Installation getInstallation() {
		return configFactory.getConfig().getSelectedInstallation();
	}

	@Override
	public List<Mod> getMods() {
		return getInstallation().getMods();
	}

	@Override
	public Mod getByUrl(URL url) {
		for (Mod mod : getMods()){
			if (ObjectUtils.nullSafeEquals(mod.getUrl(), url)){
				return mod;
			}
		}
		return null;
	}

}
