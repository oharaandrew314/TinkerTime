package io.andrewohara.tinkertime.db;

import io.andrewohara.tinkertime.models.ConfigData;
import io.andrewohara.tinkertime.models.ConfigFactory;
import io.andrewohara.tinkertime.models.Installation;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;

import org.flywaydb.core.internal.util.ObjectUtils;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;

public class DaoModLoader implements ModLoader {

	private final Dao<Mod, Integer> modDao;
	private final Dao<Installation, Integer> installationDao;
	private final ConfigFactory configFactory;

	@Inject
	DaoModLoader(Dao<Mod, Integer> modDao, Dao<Installation, Integer> installationDao, ConfigFactory configFactory){
		this.modDao = modDao;
		this.installationDao = installationDao;
		this.configFactory = configFactory;
	}

	@Override
	public void updateMod(Mod mod) {
		try {
			modDao.createOrUpdate(mod);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void deleteMod(Mod mod) {
		try {
			modDao.delete(mod);
			Installation installation = getInstallation();
			installation.unlinkMod(mod);
			installationDao.update(installation);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void changeInstallation(Installation installation) {
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
