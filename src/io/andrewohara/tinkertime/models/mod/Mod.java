package io.andrewohara.tinkertime.models.mod;

import io.andrewohara.common.version.Version;
import io.andrewohara.tinkertime.models.Installation;
import io.andrewohara.tinkertime.models.ModFile;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import com.j256.ormlite.field.DataType;
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

	public static final Dimension MAX_IMAGE_SIZE = new Dimension(250, 250);

	@DatabaseField(generatedId=true)
	private int id;

	@DatabaseField
	private Date updatedOn;

	@DatabaseField
	private String name, creator, modVersion, kspVersion;

	@DatabaseField(canBeNull = false)
	private String url;

	@DatabaseField
	private boolean updateAvailable = false, builtIn = false;

	@DatabaseField(foreign = true, canBeNull = false)
	private Installation installation;

	@ForeignCollectionField
	private Collection<ModFile> modFiles = new LinkedList<>();
	private Collection<ModFile> cachedModFiles;

	@DatabaseField(dataType = DataType.BYTE_ARRAY)
	private byte[] imageBytes;

	@DatabaseField(dataType = DataType.LONG_STRING)
	private String readmeText = "";

	// Required by ormlite
	Mod() { }

	public Mod(URL url, Installation installation){
		this(url, installation, false);
	}

	public Mod(URL url, Installation installation, boolean builtIn){
		this.url = url != null ? url.toString() : null;
		this.installation = installation;
		this.builtIn = builtIn;
	}

	public void update(String name, String creator, Date updatedOn, Version modVersion, String kspVersion){
		this.name = name;
		this.creator = creator;
		this.updatedOn = updatedOn;
		this.kspVersion = kspVersion;
		this.modVersion = modVersion != null ? modVersion.getNormalVersion() : null;
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
			return null;
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
		return getZipPath().toFile().exists();
	}

	public boolean isEnabled(){
		if (getModFiles().isEmpty()) return false;

		for (ModFile modFile : getModFiles()){
			if (!modFile.getDestPath().toFile().exists()){
				return false;
			}
		}
		return true;
	}

	public Collection<ModFile> getModFiles(){
		if (cachedModFiles == null){
			cachedModFiles = new LinkedList<>(modFiles);
		}
		return cachedModFiles;
	}

	public void setModFiles(Collection<ModFile> modFiles){
		this.modFiles = modFiles;
		this.cachedModFiles = modFiles;
	}

	public void setImage(BufferedImage image) throws IOException{
		if (image != null){
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			ImageIO.write(image, "jpg", baos);
			this.imageBytes = baos.toByteArray();
		} else {
			this.imageBytes = null;
		}
	}

	public BufferedImage getImage(){
		try {
			return ImageIO.read(new ByteArrayInputStream(imageBytes));
		} catch (IOException | NullPointerException e) {
			return null;
		}
	}

	public String getReadmeText(){
		return readmeText;
	}

	public void setReadmeText(String text) {
		this.readmeText = text;
	}

	public boolean isBuiltIn(){
		return builtIn;
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
		return new Integer(id).hashCode();
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
