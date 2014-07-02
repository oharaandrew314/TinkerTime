package aohara.tinkertime.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;

import aohara.common.AbstractConfig;
import aohara.tinkertime.TinkerTime;
import aohara.tinkertime.models.ModApi;
import aohara.tinkertime.views.DirectoryChooser;

public class Config extends AbstractConfig {
	
	public static final Collection<String> EXES = new LinkedList<>();
	static {
		EXES.add("KSP.exe");
		EXES.add("KSP.app");
		EXES.add("KSP.x86_64");
		EXES.add("KSP.x86");
	}
	
	public Config(){
		super(TinkerTime.NAME);
		setLoadOnGet(true);
	}
	
	public void setKerbalPath(Path path) throws IllegalPathException {
		if (path != null && EXES.contains(path.getFileName().toFile().getName())){
			set("kerbalPath", path.getParent().toFile().getPath());
		} else if (path != null && getKerbalExePath(path) != null){
			set("kerbalPath", path.toFile().getPath());
		} else {
			throw new IllegalPathException("Kerbal Path must contain the KSP executable");
		}
	}
	
	public void setModsPath(Path path) throws IllegalPathException {
		if (path != null && path.toFile().isDirectory()){
			set("modsPath", path.toFile().getPath());
		} else {
			throw new IllegalPathException("Mods Path must be a directory");
		}
	}
	
	private Path getKerbalPath(){
		if (hasProperty("kerbalPath")){
			return Paths.get(getProperty("kerbalPath"));
		}
		return null;
	}
	
	public Path getGameDataPath(){
		return getKerbalPath().resolve("GameData");
	}
	
	public Path getKerbalExePath(){
		return getKerbalExePath(getKerbalPath());
	}
	
	private Path getKerbalExePath(Path kerbalPath){
		Path exePath = null;
		
		for (String exe : EXES){
			exePath = kerbalPath.resolve(exe);
			if (exePath.toFile().exists()){
				return exePath;
			}
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
		if (config.getModsPath() == null || config.getKerbalPath() == null){
			new DirectoryChooser().setVisible(true);
		}
	}

}
