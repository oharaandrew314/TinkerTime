package io.andrewohara.tinkertime.models;

import io.andrewohara.common.version.Version;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Model for holding Mod information and status.
 *
 * Two flags can be set: enabled, and update available.
 * The Application will display this mod differently depending on the state
 * of these flags.
 *
 * @author Andrew O'Hara
 */
@DatabaseTable(tableName = "mods")
public class Mod implements Comparable<Mod> {

	public static final int MODULE_MANAGER_ID = 3141;

	@DatabaseField(generatedId=true)
	private int id;

	@DatabaseField
	private Date updatedOn;

	@DatabaseField
	private String name, creator, modVersion, kspVersion, newestFileName, url;

	@DatabaseField
	private boolean updateAvailable = false;

	@DatabaseField(foreign = true)
	private Installation installation;

	// Required by ormlite
	Mod() { }

	public Mod(
			String modName, String newestFileName, String creator,
			URL pageUrl, Date updatedOn, String kspVersion, Version version
			){
		this(Integer.MIN_VALUE, modName, newestFileName, creator, pageUrl, updatedOn, kspVersion, version);
	}

	public Mod(
			int id, String modName, String newestFileName, String creator,
			URL pageUrl, Date updatedOn, String kspVersion, Version version
			){
		this.id = id;
		this.newestFileName = newestFileName;
		this.updatedOn = updatedOn;
		this.url = pageUrl.toString();
		this.name = modName;
		this.creator = creator;
		this.kspVersion = kspVersion;
		this.modVersion = version != null ? version.getNormalVersion() : null;
	}

	public int getId(){
		return id;
	}

	public String getName(){
		return name;
	}

	public String getNewestFileName(){
		return newestFileName;
	}

	public String getCreator(){
		return creator;
	}

	public URL getUrl(){
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public Date getUpdatedOn(){
		return updatedOn;
	}

	public Version getModVersion(){
		return modVersion != null ? Version.valueOf(modVersion) : null;
	}

	/**
	 * Returns the version of KSP that this mod version supports.
	 * @return supported KSP version
	 */
	public String getSupportedVersion(){
		return kspVersion;
	}

	public boolean isUpdateable(){
		return url != null;
	}

	public boolean isUpdateAvailable(){
		return updateAvailable;
	}

	public void setUpdateAvailable(boolean updateAvailable){
		this.updateAvailable = updateAvailable;
	}

	public Path getCachedImagePath(ConfigData config){
		return config.getImageCachePath().resolve(getId() + ".jpg");
	}

	public boolean isBuiltIn(){
		return MODULE_MANAGER_ID == getId();
	}

	////////////////
	// Comparable //
	////////////////

	@Override
	public int compareTo(Mod other) {
		return Integer.compare(getId(), other.getId());
	}

	////////////
	// Object //
	////////////

	@Override
	public String toString(){
		return name;
	}

	@Override
	public int hashCode(){
		return Integer.hashCode(id);
	}

	@Override
	public boolean equals(Object o){
		if (o instanceof Mod){
			Mod mod = (Mod) o;
			return getId() == mod.getId() || name.equals(mod.name);
		}
		return false;
	}
}
