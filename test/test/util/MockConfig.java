package test.util;

import java.nio.file.Path;
import java.nio.file.Paths;

import test.UnitTestSuite;
import aohara.tinkertime.Config;

public class MockConfig extends Config {
	
	private final Path modsListPath = UnitTestSuite.getTempFile("mods", ".json");
	
	@Override
	public Path getGameDataPath(){
		return Paths.get("/");
	}
	
	@Override
	public Path getModsZipPath(){
		return Paths.get("test/res/zips");
	}
	
	public Path getModsListPath(){
		return modsListPath;
	}
}