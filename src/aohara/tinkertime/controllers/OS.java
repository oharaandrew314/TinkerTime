package aohara.tinkertime.controllers;

public class OS {
	
	public enum OsType { Windows, Osx, Linux };
	
	public static OsType getOs(){
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")){
			return OsType.Windows;
		} else if (os.contains("mac")){
			return OsType.Osx;
		} else if (os.contains("nux")){
			return OsType.Linux;
		} else {
			throw new IllegalStateException("Cannot recognise os: " + os);
		}
	}
}
