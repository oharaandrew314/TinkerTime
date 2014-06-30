package test.util;

import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.nio.file.Path;

import test.UnitTestSuite;
import aohara.tinkertime.config.Config;

public class MockConfig extends Config {
	
	private final Path gameDataPath, modsPath;
	
	public static Config getSpy() throws IOException {
		Path gameDataPath = UnitTestSuite.getTempDir("GameData");
		Path modsPath = UnitTestSuite.getTempDir("mods");
		
		return spy(new MockConfig(gameDataPath, modsPath));
	}
	
	public MockConfig(Path kerbalPath, Path modsPath){
		this.gameDataPath = kerbalPath;
		this.modsPath = modsPath;	
	}
	
	@Override
	public Path getGameDataPath(){
		return gameDataPath;
	}
	
	@Override
	public Path getModsPath(){
		return modsPath;
	}

}
