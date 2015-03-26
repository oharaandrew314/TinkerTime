package aohara.tinkertime.models;

import java.net.URL;
import java.nio.file.Path;
import java.util.Date;

import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.crawlers.Crawler;

/**
 * Model for holding Mod information and status.
 * 
 * Two flags can be set: enabled, and update available.
 * The Application will display this mod differently depending on the state
 * of these flags.
 * 
 * @author Andrew O'Hara
 */
public class Mod implements FileUpdateListener {
	
	public final String id;
	private Date updatedOn;
	private String name, creator, supportedVersion, newestFileName;
	private URL pageUrl;
	private boolean enabled = false;
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
	
	public String getName(){
		return name;
	}

	public String getCreator() {
		return creator;
	}
	
	public String getNewestFileName() {
		return newestFileName;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}
	
	public URL getPageUrl() {
		return pageUrl;
	}
	
	public boolean isDownloaded(TinkerConfig config){
		Path zipPath = getCachedZipPath(config);
		if (zipPath != null){
			return zipPath.toFile().exists();
		}
		return false;
	}
	
	public Path getCachedZipPath(TinkerConfig config){
		if (getNewestFileName() != null){
			String safePathFileName = getNewestFileName().replaceAll(":", "").replaceAll("/", "");
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
		return getPageUrl() != null;
	}
	
	// -- Other Methods --------------------

	/**
	 * Get this mod origin domain (from {@link aohara.tinkertime.crawlers.Constants#ACCEPTED_MOD_HOSTS})
	 * @return mod origin domain or {@code null} if not in {@code ACCEPTED_MOD_HOSTS}
	 */
	public String getOriginSite() {
		if(getPageUrl() != null && getPageUrl().getHost() != null) {
			return getPageUrl().getHost();
		}
		return null;
	}

	public boolean isEnabled(){
		return enabled;
	}
	
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}
	
	@Override
	public void setUpdateAvailable(Crawler<?> crawler){
		updateAvailable = true;
	}
	
	public boolean isUpdateAvailable(){
		return updateAvailable;
	}
	
	@Override
	public String toString(){
		return getName();
	}
	
	@Override
	public int hashCode(){
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		return o instanceof Mod && ((Mod)o).id.equals(id);
	}
}
