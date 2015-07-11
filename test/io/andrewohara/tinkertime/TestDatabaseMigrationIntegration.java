package io.andrewohara.tinkertime;

import io.andrewohara.tinkertime.TinkerTimeLauncher.DatabaseMigrator;
import io.andrewohara.tinkertime.db.DbConnectionString;

import org.junit.Before;
import org.junit.Test;

public class TestDatabaseMigrationIntegration {

	private DatabaseMigrator migrator;

	@Before
	public void setup(){
		DbConnectionString connectionString = new DbConnectionString(){
			@Override
			public String getUrl() {
				return "jdbc:h2:mem:";
			}
		};

		migrator = new DatabaseMigrator(connectionString);
	}

	@Test
	public void test(){
		migrator.run();
	}

}
