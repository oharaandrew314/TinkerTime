package aohara.tinkertime.controllers.fileUpdater;

import java.io.File;
import java.nio.file.Path;

import aohara.tinkertime.Config;

/**
 * Strategy for obtaining the currently installed version of ModuleManager.
 * 
 * @author Andrew O'Hara
 */
public class CurrentModuleManager implements CurrentVersion {
	
	public static final String MODULE_MANAGER = "ModuleManager";
	private final Config config;
	
	public CurrentModuleManager(Config config){
		this.config = config;
	}
	
	public Path getPath(){
		for (File file : config.getGameDataPath().toFile().listFiles()){
			if (file.getName().toLowerCase().startsWith(MODULE_MANAGER.toLowerCase())){
				return file.toPath();
			}
		}
		return null;
	}

	@Override
	public String getVersion() {
		Path path = getPath();
		if (path != null){
			return path.toFile().getName();
		}
		return null;
	}

	@Override
	public boolean exists() {
		return getPath() != null;
	}
}
