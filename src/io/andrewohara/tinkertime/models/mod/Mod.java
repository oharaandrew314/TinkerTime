package io.andrewohara.tinkertime.models.mod;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

import io.andrewohara.common.version.Version;
import io.andrewohara.tinkertime.TinkerTimeLauncher;
import io.andrewohara.tinkertime.models.Installation;
import io.andrewohara.tinkertime.models.ModFile;

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
public class Mod extends BaseDaoEnabled<Mod, Integer> implements Comparable<Mod> {

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

	// Used by ormlite
	Mod() { }

	public Mod(URL url, Installation installation, Dao<Mod, Integer> dao) throws SQLException{
		this(url, installation, false, dao);
	}

	public Mod(URL url, Installation installation, boolean builtIn, Dao<Mod, Integer> dao) throws SQLException{
		this.url = url != null ? url.toString() : null;
		this.installation = installation;
		if (installation != null) installation.addMod(this);
		this.builtIn = builtIn;

		setDao(dao);
	}

	// Used only for updating the TinkerTime App
	public static Mod newModManagerMod(){
		Mod mod = new Mod();
		mod.url = TinkerTimeLauncher.DOWNLOAD_URL;
		mod.modVersion = TinkerTimeLauncher.VERSION.getNormalVersion();
		return mod;
	}

	/////////////
	// Setters //
	/////////////

	public void update(ModUpdateData updateData) throws SQLException{
		this.name = updateData.name;
		this.creator = updateData.creator;
		this.updatedOn = updateData.updatedOn;
		this.kspVersion = updateData.kspVersion;
		this.modVersion = updateData.modVersion != null ? updateData.modVersion.getNormalVersion() : null;
	}

	public void setUpdateAvailable(boolean updateAvailable) throws SQLException{
		this.updateAvailable = updateAvailable;
	}

	public void setModFiles(Collection<ModFile> modFiles) throws SQLException{
		this.modFiles = modFiles;
		this.cachedModFiles = modFiles;
	}

	public void setImage(BufferedImage image) throws IOException, SQLException{
		if (image != null){
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos);
			this.imageBytes = baos.toByteArray();
		} else {
			this.imageBytes = null;
		}
	}

	public void setReadmeText(String text) throws SQLException {
		this.readmeText = text;
	}

	/////////////
	// Getters //
	/////////////

	public int getId(){
		return id;
	}

	public String getName(){
		if (name == null){
			return (url != null ? getUrl().getHost(): "Local") + " Mod";
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

	public boolean isBuiltIn(){
		return builtIn;
	}

	/////////
	// Dao //
	/////////

	public int commit() throws SQLException{
		if (dao != null){
			return getId() == 0 ? create() : update();
		}
		System.err.println("No Dao configured to update " + getName());
		return 0;
	}

	@Override
	public int delete() throws SQLException {
		if (installation != null){
			installation.removeMod(this);
		}
		return super.delete();
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
		return getName();
	}

	@Override
	public int hashCode(){
		return new Integer(id).hashCode();
	}

	@Override
	public boolean equals(Object o){
		if (o instanceof Mod){
			Mod mod = (Mod) o;
			return getId() == mod.getId() || getName().equals(mod.getName());
		}
		return false;
	}
}
