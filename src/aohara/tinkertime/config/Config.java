package aohara.tinkertime.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import aohara.common.AbstractConfig;
import aohara.tinkertime.TinkerTime;
import aohara.tinkertime.models.ModApi;
import aohara.tinkertime.views.DirectoryChooser;

public class Config extends AbstractConfig {
	
	public Config(){
		super(TinkerTime.NAME);
		setLoadOnGet(true);
	}
	
	public void setGameDataPath(Path path) throws IllegalPathException {
		if (path != null && path.endsWith("GameData")){
			set("gameDataPath", path.toString());
		} else if (path != null && path.resolve("GameData").toFile().exists()){
			setGameDataPath(path.resolve("GameData"));
		} else {
			throw new IllegalPathException("Kerbal Path must contain the GameData folder");
		}
	}
	
	public void setModsPath(Path path) throws IllegalPathException {
		if (path != null && path.toFile().isDirectory()){
			set("modsPath", path.toFile().getPath());
		} else {
			throw new IllegalPathException("Mods Path must be a directory");
		}
	}
	
	public Path getGameDataPath(){
		if (hasProperty("gameDataPath")){
			return Paths.get(getProperty("gameDataPath"));
		}
		return null;
	}
	
	public Path getModZipPath(ModApi mod){
		return new Config().getModsPath().resolve(mod.getNewestFile());
	}
	
	public Path getModsPath(){
		if (hasProperty("modsPath")){
			return Paths.get(getProperty("modsPath"));
		}
		return null;
	}
	
	public void set(String key, String value){
		setProperty(key, value);
		save();
	}
	
	@SuppressWarnings("serial")
	public class IllegalPathException extends Exception {
		public IllegalPathException(String message){
			super(message);
		}
	}
	
	public static void verifyConfig(){
		Config config = new Config();
		if (config.getModsPath() == null || config.getGameDataPath() == null){
			updateConfig(false, true);
		}
	}
	
	public static void updateConfig(boolean restartOnSuccess, boolean exitOnCancel){
		new DirectoryChooser(new Config(), restartOnSuccess, exitOnCancel).setVisible(true);
	}

}
