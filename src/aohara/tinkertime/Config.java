package aohara.tinkertime;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFileChooser;

import aohara.common.AbstractConfig;
import aohara.common.options.Constraints;
import aohara.common.options.Option;
import aohara.common.options.OptionInput;
import aohara.common.options.OptionSaveStrategy;
import aohara.common.options.OptionsWindow;

/**
 * Stores and Retrieves User Configuration Data.
 * 
 * Holds data related to Mod Zip File Storage and Mod Installation Directory.
 * 
 * @author Andrew O'Hara
 */
public class Config extends AbstractConfig {
	
	private static final String
		GAMEDATA_PATH = "gamedataPath",
		AUTO_UPDATE_MM = "autoUpdateMM",
		AUTO_CHECK_FOR_MOD_UPDATES = "autoCheckForModUpdates",
		NUM_CONCURRENT_DOWNLOADS = "numConcurrentDownloads";
			
	
	public Config(){
		super(TinkerTime.NAME);
		setLoadOnGet(true);
	}
	
	// -- Getters -------------------------------------------------------
	
	public Path getGameDataPath(){
		return Paths.get(getProperty(GAMEDATA_PATH));	
	}
	
	public Path getModsZipPath(){
		Path path = getFolder().resolve("mods");
		path.toFile().mkdirs();
		return path;
	}
	
	public Path getImageCachePath(){
		Path path = getFolder().resolve("imageCache");
		path.toFile().mkdirs();
		return path;
	}
	
	public Path getModsListPath(){
		return getGameDataPath().resolve("TinkerTime.json");
	}
	
	public boolean autoUpdateModuleManager(){
		return Boolean.parseBoolean(getProperty(AUTO_UPDATE_MM));
	}
	
	public boolean autoCheckForModUpdates(){
		return Boolean.parseBoolean(getProperty(AUTO_CHECK_FOR_MOD_UPDATES)); 
	}
	
	public int numConcurrentDownloads(){
		return Integer.parseInt(getProperty(NUM_CONCURRENT_DOWNLOADS));
	}
	
	// -- Setters ----------------------------------------------------------
	
	@Override
	public void setProperty(String key, String value){
		super.setProperty(key, value);
		save();
	}
	
	// -- Verification ----------------------------------------------------
	
	@Override
	public void verifyConfig(){
		if (!hasProperty(AUTO_UPDATE_MM)){
			setProperty(AUTO_UPDATE_MM, Boolean.toString(true));
		}
		
		if (!hasProperty(AUTO_CHECK_FOR_MOD_UPDATES)){
			setProperty(AUTO_CHECK_FOR_MOD_UPDATES, Boolean.toString(true));
		}
		
		if (!hasProperty(NUM_CONCURRENT_DOWNLOADS)){
			setProperty(NUM_CONCURRENT_DOWNLOADS, Integer.toString(4));
		}
		
		if (!hasProperty(GAMEDATA_PATH) || getProperty(GAMEDATA_PATH) == null || getProperty(GAMEDATA_PATH).isEmpty()){
			updateConfig(true, true);
		}
	}
	
	public void updateConfig(boolean restartOnSuccess, boolean exitOnCancel){		
		Set<OptionInput> optionInputs = new HashSet<>();
		
		// GameData Path Option
		Option option = new Option(
			"GameData Path",
			hasProperty(GAMEDATA_PATH) ? getGameDataPath().toString() : null,
			new OptionSaveStrategy.ConfigStrategy(this, GAMEDATA_PATH)
		);
		option.addConstraint(new Constraints.NotNull(option));
		option.addConstraint(new Constraints.EnsurePathExists(option, true));
		optionInputs.add(new OptionInput.FileChooserInput(option, JFileChooser.DIRECTORIES_ONLY));
		
		// Auto Update Module Manager Option
		option = new Option(
			"Update Module Manager on Startup",
			Boolean.toString(autoUpdateModuleManager()),
			new OptionSaveStrategy.ConfigStrategy(this, AUTO_UPDATE_MM) 
		);
		optionInputs.add(new OptionInput.TrueFalseInput(option));
		
		// Auto Check for Mod Updates Option
		option = new Option(
			"Check For Updates on Startup",
			Boolean.toString(autoCheckForModUpdates()),
			new OptionSaveStrategy.ConfigStrategy(this, AUTO_CHECK_FOR_MOD_UPDATES) 
		);
		optionInputs.add(new OptionInput.TrueFalseInput(option));
		
		// Number of Concurrent Downloads Option
		option = new Option(
			"Number of Concurrent Downloads",
			Integer.toString(numConcurrentDownloads()),
			new OptionSaveStrategy.ConfigStrategy(this, NUM_CONCURRENT_DOWNLOADS)
		);
		option.addConstraint(new Constraints.EnsureIntRange(option, 0, Integer.MAX_VALUE));
		optionInputs.add(new OptionInput.TextFieldInput(option));
		
		new OptionsWindow("Options", optionInputs, restartOnSuccess, exitOnCancel).toDialog();
	}
	
	// -- Exceptions -------------------------------------------------
	
	@SuppressWarnings("serial")
	public static class IllegalPathException extends Exception {
		private IllegalPathException(String message){
			super(message);
		}
	}
}
