package aohara.tinkertime;


import java.io.IOException;
import java.nio.file.Path;

import javax.swing.JFileChooser;

import aohara.common.config.Config;
import aohara.common.config.ConfigBuilder;
import aohara.common.config.OptionsWindow;
import aohara.common.config.Constraint.InvalidInputException;

import com.google.inject.Singleton;

/**
 * Stores and Retrieves User Configuration Data.
 * 
 * @author Andrew O'Hara
 */
@Singleton
public class TinkerConfig {
	 
	private static final String
		GAMEDATA_PATH = "GameData Path",
		AUTO_CHECK_FOR_MOD_UPDATES = "Check for Mod Updates on Startup",
		NUM_CONCURRENT_DOWNLOADS = "Number of Concurrent Downloads",
		KSP_WIN_LAUNCH_ARGS = "KSP Launch Arguments",
		WIN_64 = "win64",
		STARTUP_CHECK_MM_UPDATES = "Check for App Updates on Startup";
		
	final Config config;
	
	public TinkerConfig(Config config){
		this.config = config;
	}
	
	public static TinkerConfig create(){
		ConfigBuilder builder = new ConfigBuilder();
		builder.addBooleanProperty(AUTO_CHECK_FOR_MOD_UPDATES, false, false, false);
		builder.addBooleanProperty(STARTUP_CHECK_MM_UPDATES, true, false, false);
		builder.addPathProperty(GAMEDATA_PATH, JFileChooser.DIRECTORIES_ONLY, null, false, false);
		builder.addIntProperty(NUM_CONCURRENT_DOWNLOADS, 4, 1, null, false, false);
		builder.addStringProperty(KSP_WIN_LAUNCH_ARGS, null, true, false);
		
		builder.addStringProperty(WIN_64, null, true, true);
		
		Config config = builder.createConfigInDocuments(
			String.format("%s Config", TinkerTime.SAFE_NAME),
			TinkerTime.NAME,
			"TinkerTime-Options.json"
		);
		
		// Verify Config
		try {
			config.reload();
		} catch (InvalidInputException | IOException e) {
			new OptionsWindow(config).toDialog();
			try {
				config.reload();
			} catch (IOException | InvalidInputException e1) {
				throw new RuntimeException(e);
			}
		}

		return new TinkerConfig(config);
	}

	// -- Getters -------------------------------------------------------
	
	public Path getGameDataPath(){
		return config.getProperty(GAMEDATA_PATH).getValueAsFile().toPath();
	}
	
	private Path getModCachePath(){
		return getSubFolder(getGameDataPath().getParent(), TinkerTime.SAFE_NAME);	
	}
	
	public Path getModsZipPath(){
		return getSubFolder(getModCachePath(), "modCache");
	}
	
	public Path getImageCachePath(){
		return getSubFolder(getModCachePath(), "imageCache");
	}
	
	public Path getModsListPath(){
		return getModCachePath().resolve("TinkerTime-mods.json");
	}
	
	private Path getSubFolder(Path parent, String subFolder){
		Path path = parent.resolve(subFolder);
		path.toFile().mkdir();
		return path;
	}
	
	public boolean autoCheckForModUpdates(){
		return config.getProperty(AUTO_CHECK_FOR_MOD_UPDATES).getValueAsBool();
	}
	
	public int numConcurrentDownloads(){
		return config.getProperty(NUM_CONCURRENT_DOWNLOADS).getValueAsInt();
	}
	
	public String getLaunchArguments(){
		return config.getProperty(KSP_WIN_LAUNCH_ARGS).getValueAsString();
	}
	
	public boolean isCheckForMMUpdatesOnStartup(){
		return config.getProperty(STARTUP_CHECK_MM_UPDATES).getValueAsBool();
	}
}
