package aohara.tinkertime.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Config {
	
	public Path getKerbalPath(){
		return Paths.get("/Users/Andrew/Desktop/Kerbal/GameData");
	}
	
	public Path getModPath(){
		return Paths.get("/Users/Andrew/Desktop/Mods");
	}

}
