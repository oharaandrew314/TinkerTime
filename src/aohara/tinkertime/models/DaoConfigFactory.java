package aohara.tinkertime.models;

import java.sql.SQLException;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;

public class DaoConfigFactory implements ConfigFactory {

	private final Dao<ConfigData, Integer> dao;

	@Inject
	DaoConfigFactory(Dao<ConfigData, Integer> dao){
		this.dao = dao;
	}

	@Override
	public ConfigData getConfig(){
		try {
			dao.createIfNotExists(new ConfigData());
			return dao.queryForId(ConfigData.CONFIG_ID);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void update(ConfigData config){
		try {
			dao.update(config);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
