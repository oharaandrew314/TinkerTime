package aohara.tinkertime.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import aoahara.common.AbstractConfig;
import aohara.tinkertime.TinkerTime;

public class Config extends AbstractConfig {
	
	public static final String KSP_EXE = "KSP.exe";
	
	public Config(){
		super(TinkerTime.NAME);
	}
	
	public void setKerbalPath(Path path) throws IllegalPathException {
		if (path != null && path.getFileName().toFile().getName().equals(KSP_EXE)){
			set("kerbalPath", path.getParent().toFile().getPath());
		} else if (path != null && path.resolve(KSP_EXE).toFile().exists()){
			set("kerbalPath", path.toFile().getPath());
		} else {
			throw new IllegalPathException("Kerbal Path must have " + KSP_EXE);
		}
	}
	
	public void setModsPath(Path path) throws IllegalPathException {
		if (path != null && path.toFile().isDirectory()){
			set("modsPath", path.toFile().getPath());
		} else {
			throw new IllegalPathException("Mods Path must be a directory");
		}
	}
	
	public Path getKerbalPath(){
		if (hasProperty("kerbalPath")){
			return Paths.get(getProperty("kerbalPath"));
		}
		return null;
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

}
