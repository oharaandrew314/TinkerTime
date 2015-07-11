package io.andrewohara.tinkertime.db;

import io.andrewohara.tinkertime.TinkerTimeLauncher;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DbConnectionStringImpl implements DbConnectionString {

	@Override
	public String getUrl() {
		Path path = Paths.get(System.getProperty("user.home"), "Documents", TinkerTimeLauncher.NAME, "TinkerTime-db");
		return String.format("jdbc:h2:file:%s", path.toString());
	}

}
