package test.util;

import java.nio.file.Path;
import java.nio.file.Paths;

import aohara.tinkertime.Config;

public class MockConfig extends Config {
	
	@Override
	public Path getGameDataPath(){
		return Paths.get("/");
	}
	
	@Override
	public Path getModsZipPath(){
		return Paths.get("/");
	}
}