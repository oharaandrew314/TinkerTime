package io.andrewohara.tinkertime.models.mod;

import io.andrewohara.common.version.Version;
import io.andrewohara.tinkertime.models.Installation;
import io.andrewohara.tinkertime.models.ModFile;
import io.andrewohara.tinkertime.models.ModImage;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
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

	@DatabaseField(generatedId=true)
	private int id;

	@DatabaseField
	private Date updatedOn;

	@DatabaseField
	private String name, creator, modVersion, kspVersion, url;

	@DatabaseField
	private boolean updateAvailable = false;

	@DatabaseField(foreign = true, canBeNull = false)
	private Installation installation;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private ModImage image;

	@ForeignCollectionField
	private Collection<ModFile> modFiles = new LinkedList<>();
	private final Collection<ModFile> cachedModFiles = new LinkedList<>();

	// Required by ormlite
	Mod() { }

	public Mod(URL url, Installation installation){
		this.url = url.toString();
		this.installation = installation;
	}

	public void update(String name, String creator, Date updatedOn, Version modVersion, String kspVersion){
		this.name = name;
		this.creator = creator;
		this.updatedOn = updatedOn;
		this.kspVersion = kspVersion;
		this.modVersion = modVersion.getNormalVersion();
	}

	public int getId(){
		return id;
	}

	public String getName(){
		if (name == null && url != null){
			return getUrl().getHost() + " Mod";
		}
		return name;
	}

	public String getCreator(){
		return creator;
	}

	public URL getUrl(){
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);  // Should not happen
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

	public Installation getInstallation(){
		return installation;
	}

	public void setUpdateAvailable(boolean updateAvailable){
		this.updateAvailable = updateAvailable;
	}

	public Path getZipPath() {
		return installation.getModZipsPath().resolve(getId() + ".zip");
	}

	public boolean isDownloaded(){
		Path zipPath = getZipPath();
		return zipPath != null && zipPath.toFile().exists();
	}

	public boolean isEnabled(){
		for (ModFile modFile : getModFiles()){
			if (!modFile.getDestPath().toFile().exists()){
				return false;
			}
		}
		return true;
	}

	public Collection<ModFile> getModFiles(){
		if (cachedModFiles.isEmpty()){
			cachedModFiles.addAll(modFiles);
		}
		return cachedModFiles;
	}

	public void setModFiles(Collection<ModFile> modFiles){
		this.modFiles = modFiles;
	}

	public void setImage(ModImage image){
		this.image = image;
	}

	public ModImage getImage(){
		return image;
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
