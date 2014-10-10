package aohara.tinkertime.models;

import java.net.URL;
import java.nio.file.Path;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;

import aohara.tinkertime.Config;

/**
 * Model for holding Mod information and status.
 * 
 * Two flags can be set: enabled, and update available.
 * The Application will display this mod differently depending on the state
 * of these flags.
 * 
 * @author Andrew O'Hara
 */
public class Mod extends UpdateableFile {
	
	public final String id;
	private String name, creator, supportedVersion;
	private URL imageUrl;
	private boolean enabled = false;
	private transient boolean updateAvailable = false;
	
	public Mod(
			String id, String modName, String newestFileName, String creator,
			URL imageUrl, URL pageUrl, Date updatedOn, String supportedVersion){
		super(newestFileName, updatedOn, pageUrl);
		this.id = id;
		update(newestFileName, updatedOn, pageUrl);
		this.name = modName;
		this.creator = creator;
		this.imageUrl = imageUrl;
		updateAvailable = false;
	}
	
	public String getName(){
		return name;
	}

	public String getCreator() {
		return creator;
	}

	public URL getImageUrl() {
		return imageUrl;
	}
	
	public boolean isDownloaded(Config config){
		return getCachedZipPath(config).toFile().exists();
	}
	
	public Path getCachedZipPath(Config config){
		return config.getModsZipPath().resolve(getNewestFileName());
	}
	
	public Path getCachedImagePath(Config config){
		String imageName = FilenameUtils.getBaseName(getPageUrl().toString());
		return config.getImageCachePath().resolve(imageName);
	}
	
	/**
	 * Returns the version of KSP that this mod version supports.
	 * @return supported KSP version
	 */
	public String getSupportedVersion(){
		return supportedVersion;
	}
	
	// -- Other Methods --------------------
	
	public boolean isEnabled(){
		return enabled;
	}
	
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}
	
	@Override
	public void setUpdateAvailable(URL pageUrl, String newestFileName){
		updateAvailable = true;
	}
	
	public boolean isUpdateAvailable(){
		return updateAvailable;
	}
	
	@Override
	public String toString(){
		return getName();
	}
}
