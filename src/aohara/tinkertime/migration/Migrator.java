package aohara.tinkertime.migration;

import org.flywaydb.core.Flyway;

import aohara.tinkertime.TinkerTime;

import com.google.inject.Inject;
import com.j256.ormlite.support.ConnectionSource;

public class Migrator {

	private final Flyway flyway;

	@Inject
	Migrator(ConnectionSource source){
		this.flyway = new Flyway();
		flyway.setLocations(getClass().getPackage().getName());
		flyway.setDataSource(TinkerTime.getDbUrl(), null, null);
	}

	public void migrate(){
		flyway.migrate();
	}
}
