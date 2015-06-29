package aohara.tinkertime.controllers;

import java.sql.SQLException;
import java.util.List;

import aohara.tinkertime.models.ConfigData;
import aohara.tinkertime.models.ConfigFactory;
import aohara.tinkertime.models.Installation;
import aohara.tinkertime.models.Mod;

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
			modDao.update(mod);
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

}
