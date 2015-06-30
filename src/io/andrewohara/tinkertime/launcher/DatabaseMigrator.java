package io.andrewohara.tinkertime.launcher;

import org.flywaydb.core.Flyway;

class DatabaseMigrator implements Runnable {

	@Override
	public void run() {
		Flyway flyway = new Flyway();
		flyway.setLocations(getClass().getPackage().getName() + "/migration");
		flyway.setDataSource(TinkerTimeLauncher.getDbUrl(), null, null);  // TODO Inject URL
		flyway.migrate();
	}
}
