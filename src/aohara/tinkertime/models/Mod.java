package aohara.tinkertime.models;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import aohara.tinkertime.TinkerConfig;

/**
 * Model for holding Mod information and status.
 * 
 * Two flags can be set: enabled, and update available.
 * The Application will display this mod differently depending on the state
 * of these flags.
 * 
 * @author Andrew O'Hara
 */
public class Mod implements Comparable<Mod> {
	
	public final Date updatedOn;
	public final String id, name, creator, supportedVersion, newestFileName;
	public final URL pageUrl;
	private transient boolean updateAvailable = false;
	
	public Mod(
		String id, String modName, String newestFileName, String creator,
		URL pageUrl, Date updatedOn, String supportedVersion
	){
		this.id = id;
		this.newestFileName = newestFileName;
		this.updatedOn = updatedOn;
		this.pageUrl = pageUrl;
		this.name = modName;
		this.creator = creator;
		this.supportedVersion = supportedVersion;
	}
	
	public boolean isDownloaded(TinkerConfig config){
		Path zipPath = getCachedZipPath(config);
		if (zipPath != null){
			return zipPath.toFile().exists();
		}
		return false;
	}
	
	public Path getCachedZipPath(TinkerConfig config){
		if (newestFileName != null){
			String safePathFileName = newestFileName.replaceAll(":", "").replaceAll("/", "");
			return config.getModsZipPath().resolve(safePathFileName);
		}
		return null;
	}
	
	public Path getCachedImagePath(TinkerConfig config){
		return config.getImageCachePath().resolve(id + ".jpg");
	}
	
	/**
	 * Returns the version of KSP that this mod version supports.
	 * @return supported KSP version
	 */
	public String getSupportedVersion(){
		return supportedVersion;
	}
	
	public boolean isUpdateable(){
		return pageUrl != null;
	}
	
	// -- Other Methods --------------------

	public boolean isEnabled(TinkerConfig config){
		// Cannot decide if the mod is enabled if the zip is missing
		if (!isDownloaded(config)){
			return false;
		}
		
		// Find all installed module names
		Set<String> installedModuleNames = new HashSet<>();
		for (File dirEntry : config.getGameDataPath().toFile().listFiles()){
			installedModuleNames.add(dirEntry.getName());
		}
		
		// If all modules in the mod are installed, then the mod is enabled
		try {
			return installedModuleNames.containsAll(
				ModStructure.inspectArchive(config, this).getModuleNames()
			);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void setUpdateAvailable(){
		updateAvailable = true;
	}
	
	public boolean isUpdateAvailable(){
		return updateAvailable;
	}
	
	@Override
	public String toString(){
		return name;
	}
	
	@Override
	public int hashCode(){
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof Mod){
			Mod mod = (Mod) o;
			return id.equals(mod.id) || name.equals(mod.name);
		}
		return false;
	}

	@Override
	public int compareTo(Mod other) {
		return id.compareTo(other.id);
	}
}
