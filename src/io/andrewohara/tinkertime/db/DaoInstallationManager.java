package io.andrewohara.tinkertime.db;

import io.andrewohara.tinkertime.models.DefaultMods;
import io.andrewohara.tinkertime.models.Installation;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.sql.SQLException;
import java.util.Collection;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;

public class DaoInstallationManager implements InstallationManager {

	private final Dao<Installation, Integer> dao;
	private final ModLoader modLoader;

	@Inject
	public DaoInstallationManager(Dao<Installation, Integer> dao, ModLoader modLoader) {
		this.dao = dao;
		this.modLoader = modLoader;
	}

	@Override
	public Collection<Installation> getInstallations() {
		try {
			return dao.queryForAll();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void delete(Installation installation) {
		try {
			dao.delete(installation);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void update(Installation installation) {
		try {
			if (dao.createOrUpdate(installation).isCreated()){
				// If new installation, add default mods
				for (Mod mod : DefaultMods.getDefaults(installation)){
					modLoader.updateMod(mod);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
