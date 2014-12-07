package test.util;

import java.nio.file.Path;
import java.nio.file.Paths;

import test.UnitTestSuite;
import aohara.tinkertime.TinkerConfig;

public class MockConfig extends TinkerConfig {
	
	private final Path modsListPath = UnitTestSuite.getTempFile("mods", ".json");
	
	public MockConfig(){
		super(null);
	}
	
	@Override
	public Path getGameDataPath(){
		return Paths.get("/");
	}
	
	@Override
	public Path getModsZipPath(){
		return Paths.get("zips");
	}
	
	public Path getModsListPath(){
		return modsListPath;
	}
}