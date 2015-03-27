package aohara.tinkertime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import aohara.common.OS;
import aohara.common.config.ConfigBuilder;
import aohara.common.config.Constraint.InvalidInputException;
import aohara.common.config.GuiConfig;

/**
 * Stores and Retrieves User Configuration Data.
 * 
 * @author Andrew O'Hara
 */
public class TinkerConfig {
	 
	private static final String
		GAMEDATA_PATH = "GameData Path",
		AUTO_CHECK_FOR_MOD_UPDATES = "Check for Mod Updates on Startup",
		NUM_CONCURRENT_DOWNLOADS = "Number of Concurrent Downloads",
		KSP_WIN_LAUNCH_ARGS = "KSP Launch Arguments",
		WIN_64 = "win64",
		STARTUP_CHECK_MM_UPDATES = "Check for App Updates on Startup";
		
	private final GuiConfig config;
	
	protected TinkerConfig(GuiConfig config){
		this.config = config;
	}
	
	public static TinkerConfig create(){
		ConfigBuilder builder = new ConfigBuilder();
		builder.addTrueFalseProperty(AUTO_CHECK_FOR_MOD_UPDATES, true, false, false);
		builder.addPathProperty(GAMEDATA_PATH, JFileChooser.DIRECTORIES_ONLY, null, false, false);
		builder.addIntProperty(NUM_CONCURRENT_DOWNLOADS, 4, 1, null, false, false);
		builder.addTrueFalseProperty(STARTUP_CHECK_MM_UPDATES, true, false, false);
		builder.addTextProperty(KSP_WIN_LAUNCH_ARGS, null, true, false);
		
		builder.addTextProperty(WIN_64, null, true, true);
		
		GuiConfig config = builder.createGuiConfigInDocuments("TinkerTime Config", TinkerTime.NAME, "TinkerTime.json");
		if (!config.isValid()){
			config.openOptionsWindow(true);
		}
		
		return new TinkerConfig(config);
	}
	
	// -- Getters -------------------------------------------------------
	
	public Path getGameDataPath(){
		return Paths.get(config.getProperty(GAMEDATA_PATH));	
	}
	
	private Path getModCachePath(){
		Path path = getGameDataPath().getParent().resolve(TinkerTime.NAME.replace(" ", ""));
		path.toFile().mkdir();
		return path;		
	}
	
	public Path getModsZipPath(){
		Path path = getModCachePath().resolve("modCache");
		path.toFile().mkdir();
		return path;
	}
	
	public Path getImageCachePath(){
		Path path = getModCachePath().resolve("imageCache");
		path.toFile().mkdir();
		return path;
	}
	
	public Path getModsListPath(){
		return getGameDataPath().resolve("TinkerTime.json");
	}
	
	public boolean autoCheckForModUpdates(){
		return Boolean.parseBoolean(config.getProperty(AUTO_CHECK_FOR_MOD_UPDATES)); 
	}
	
	public int numConcurrentDownloads(){
		return Integer.parseInt(config.getProperty(NUM_CONCURRENT_DOWNLOADS));
	}
	
	public boolean use64BitGame(){
		switch(OS.getOs()){
		case Windows:
			// If Windows, only run 64-bit if user chooses to.  Cache user's choice.
			boolean is64Bit = System.getenv("ProgramFiles(x86)") != null; 
			if (is64Bit){
				if (config.getProperty(WIN_64) == null){
					boolean use64 = JOptionPane.showConfirmDialog(
						null,
						"Tinker Time has detected that you are runing a 64-bit system.\n" +
						"Would you like to run the 64-bit version of KSP?\n\n" +
						"Caution: The 64-bit Unity Engine for Windows is still unstable.",
						"Launch 64-bit KSP?",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE
					) == JOptionPane.YES_OPTION;
					try {
						config.setProperty(WIN_64, Boolean.toString(use64));
						config.save();
					} catch (InvalidInputException e) {
					}
				} else {
					return Boolean.parseBoolean(config.getProperty(WIN_64));
				}
			} else {
				return false;
			}
		case Linux:
			// If Linux, run 64-bit if system is 64-bit
			try(
				BufferedReader r = new BufferedReader(new InputStreamReader(
					Runtime.getRuntime().exec("uname -m").getInputStream()
				))
			){
				return r.readLine().toLowerCase().equals("x86_64");
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		case Osx:
			// If OSX, always run 64-bit
			return true;
		default:
			throw new IllegalStateException();
		}
	}
	
	public String getLaunchArguments(){
		return config.getProperty(KSP_WIN_LAUNCH_ARGS);
	}
	
	public boolean isCheckForMMUpdatesOnStartup(){
		return Boolean.parseBoolean(config.getProperty(STARTUP_CHECK_MM_UPDATES));
	}
	
	// -- Verification ----------------------------------------------------
	
	public void updateConfig(boolean exitOnCancel){
		config.openOptionsWindow(exitOnCancel);
	}
}
