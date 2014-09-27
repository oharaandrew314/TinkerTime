package aohara.tinkertime;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFileChooser;

import aohara.common.AbstractConfig;
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
		AUTO_CHECK_FOR_MOD_UPDATES = "autoCheckForModUpdates";
			
	
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
		
		if (!hasProperty(GAMEDATA_PATH)){
			updateConfig(true, true);
		}
	}
	
	public void updateConfig(boolean restartOnSuccess, boolean exitOnCancel){		
		Set<OptionInput> optionInputs = new HashSet<>();
		
		// GameData Path Option
		Option option = new Option(
			"GameData Path",
			getGameDataPath().toString(),
			new OptionSaveStrategy.ConfigStrategy(this, GAMEDATA_PATH)
		);
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
