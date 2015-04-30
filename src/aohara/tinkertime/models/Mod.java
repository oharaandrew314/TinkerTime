package aohara.tinkertime.models;

import java.net.URL;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;

import aohara.common.VersionParser;
import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.crawlers.Crawler;

import aohara.common.Version;

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
	public boolean updateAvailable = false;
	
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
	
	public static Mod newTempMod(Path zipPath){
		String fileName = zipPath.getFileName().toString();
		String prettyName = fileName;
		if (prettyName.indexOf(".") > 0) {
			prettyName = prettyName.substring(0, prettyName.lastIndexOf("."));
		}
		return new Mod(
			fileName, prettyName, fileName, null, null,
			Calendar.getInstance().getTime(), null,
			Version.valueOf(VersionParser.parseVersionString(prettyName))
		);
	}
	
	public static Mod newTempMod(URL url){
		return newTempMod(url, null);
	}
	
	public static Mod newTempMod(URL url, Version version){
		return new Mod(
				Crawler.urlToId(url), String.format("New %s Mod",
				url.getHost()), null, null, url, null, null, version
			);
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
