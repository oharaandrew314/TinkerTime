package io.andrewohara.tinkertime.controllers;

import java.net.URL;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Collection;

import org.flywaydb.core.internal.util.ObjectUtils;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;

import io.andrewohara.tinkertime.models.ConfigData;
import io.andrewohara.tinkertime.models.mod.Mod;
import io.andrewohara.tinkertime.models.mod.ModUpdateData;

public class ModLoader {

	private final Dao<Mod, Integer> modsDao;
	private final ConfigData config;

	@Inject
	ModLoader(Dao<Mod, Integer> modDao, ConfigData config){
		this.modsDao = modDao;
		this.config = config;
	}

	public Mod get(int id){
		try {
			return modsDao.queryForId(id);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Collection<Mod> getMods() {
		return config.getSelectedInstallation().getMods();
	}

	public Mod getByUrl(URL url) {
		for (Mod mod : getMods()){
			if (ObjectUtils.nullSafeEquals(mod.getUrl(), url)){
				return mod;
			}
		}
		return null;
	}

	///////////////
	// Factories //
	///////////////

	public Mod newLocalMod(Path path) throws SQLException{
		Mod mod = new Mod(null, config.getSelectedInstallation(), modsDao);
		mod.update(new ModUpdateData(path.getFileName().toString(), null, null, null, null));
		return mod;
	}

	public Mod newMod(URL url) throws SQLException {
		Mod mod = getByUrl(url);
		if (mod == null){
			mod = new Mod(url, config.getSelectedInstallation(), modsDao);
		}
		return mod;
	}
}
