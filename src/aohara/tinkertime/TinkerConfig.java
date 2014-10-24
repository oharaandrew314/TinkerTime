package aohara.tinkertime;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFileChooser;

import aohara.common.config.Config;
import aohara.common.config.ConfigBuilder;

/**
 * Stores and Retrieves User Configuration Data.
 * 
 * @author Andrew O'Hara
 */
public class TinkerConfig {
	 
	private static final String
		GAMEDATA_PATH = "GamaData Path",
		AUTO_UPDATE_MM = "Auto-Update Module Manager",
		AUTO_CHECK_FOR_MOD_UPDATES = "Check for Mod Updates on Startup",
		NUM_CONCURRENT_DOWNLOADS = "Number of Concurrent Downloads";
		
	private final Config config;
	
	protected TinkerConfig(Config config){
		this.config = config;
	}
	
	public static TinkerConfig create(){
		ConfigBuilder builder = new ConfigBuilder();
		builder.addTrueFalseProperty(AUTO_UPDATE_MM, true, false);
		builder.addTrueFalseProperty(AUTO_CHECK_FOR_MOD_UPDATES, true, false);
		builder.addPathProperty(GAMEDATA_PATH, JFileChooser.DIRECTORIES_ONLY, null, false);
		builder.addIntProperty(NUM_CONCURRENT_DOWNLOADS, 4, 1, null, false);
		
		Config config = builder.createConfigInDocuments(TinkerTime.NAME, "TinkerTime.properties");
		if (!config.isValid()){
			config.openOptionsWindow(false, true);
		}
		
		return new TinkerConfig(config);
	}
	
	// -- Getters -------------------------------------------------------
	
	public Path getGameDataPath(){
		return Paths.get(config.getProperty(GAMEDATA_PATH));	
	}
	
	public Path getModsZipPath(){
		Path path = config.getFolder().resolve("mods");
		path.toFile().mkdirs();
		return path;
	}
	
	public Path getImageCachePath(){
		Path path = config.getFolder().resolve("imageCache");
		path.toFile().mkdirs();
		return path;
	}
	
	public Path getModsListPath(){
		return getGameDataPath().resolve("TinkerTime.json");
	}
	
	public boolean autoUpdateModuleManager(){
		return Boolean.parseBoolean(config.getProperty(AUTO_UPDATE_MM));
	}
	
	public boolean autoCheckForModUpdates(){
		return Boolean.parseBoolean(config.getProperty(AUTO_CHECK_FOR_MOD_UPDATES)); 
	}
	
	public int numConcurrentDownloads(){
		return Integer.parseInt(config.getProperty(NUM_CONCURRENT_DOWNLOADS));
	}
	
	

	// -- Verification ----------------------------------------------------
	
	public void updateConfig(boolean restartOnSuccess, boolean exitOnCancel){
		config.openOptionsWindow(restartOnSuccess, exitOnCancel);
	}
}
