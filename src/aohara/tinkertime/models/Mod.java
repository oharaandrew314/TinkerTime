package aohara.tinkertime.models;

import java.net.URL;
import java.nio.file.Path;
import java.util.Date;

import aohara.tinkertime.TinkerConfig;

import com.github.zafarkhaja.semver.Version;

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
	public final String id, name, creator, kspVersion, newestFileName;
	public final URL pageUrl;
	private final String version;
	public transient boolean updateAvailable = false;
	
	public Mod(
		String id, String modName, String newestFileName, String creator,
		URL pageUrl, Date updatedOn, String kspVersion, Version version
	){
		this.id = id;
		this.newestFileName = newestFileName;
		this.updatedOn = updatedOn;
		this.pageUrl = pageUrl;
		this.name = modName;
		this.creator = creator;
		this.kspVersion = kspVersion;
		this.version = version != null ? version.getNormalVersion() : null;
	}
	
	public Path getCachedImagePath(TinkerConfig config){
		return config.getImageCachePath().resolve(id + ".jpg");
	}
	
	/**
	 * Returns the version of KSP that this mod version supports.
	 * @return supported KSP version
	 */
	public String getSupportedVersion(){
		return kspVersion;
	}
	
	public boolean isUpdateable(){
		return pageUrl != null;
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
	
	public Version getVersion(){
		return version != null ? Version.valueOf(version) : null;
	}

	@Override
	public int compareTo(Mod other) {
		return id.compareTo(other.id);
	}
}
