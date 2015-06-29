package aohara.tinkertime.models;

import java.nio.file.Path;

import aohara.tinkertime.TinkerTime;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "config")
public class ConfigData {

	public static final Integer CONFIG_ID = 1;

	/////////////
	// Columns //
	/////////////

	@DatabaseField(id=true)
	private int id = CONFIG_ID;

	@DatabaseField(defaultValue = "true")
	private boolean checkForAppUpdatesOnStartup, checkForModUpdatesOnStartup;

	@DatabaseField(defaultValue = "4")
	private int numConcurrentDownloads;

	@DatabaseField(foreign = true, foreignAutoRefresh=true)
	private Installation selectedInstallation;

	@DatabaseField
	private String launchArguments;

	///////////////
	// Interface //
	///////////////

	public Path getModsZipPath(){
		return getSubFolder(getModCachePath(), "modCache");
	}

	public Path getImageCachePath(){
		return getSubFolder(getModCachePath(), "imageCache");
	}

	public Path getModsListPath(){
		return getModCachePath().resolve("TinkerTime-mods.json");
	}

	public boolean isCheckForAppUpdatesOnStartup(){
		return checkForAppUpdatesOnStartup;
	}

	public boolean isCheckForModUpdatesOnStartup(){
		return checkForModUpdatesOnStartup;
	}

	public int getNumConcurrentDownloads(){
		return numConcurrentDownloads;
	}

	public Installation getSelectedInstallation() {
		return selectedInstallation;
	}

	public void setSelectedInstallation(Installation newInstallation){
		this.selectedInstallation = newInstallation;
	}

	public String getLaunchArguments(){
		return launchArguments;
	}

	/////////////
	// Helpers //
	/////////////

	private Path getSubFolder(Path parent, String subFolder){
		Path path = parent.resolve(subFolder);
		path.toFile().mkdir();
		return path;
	}

	private Path getModCachePath(){
		return getSubFolder(getSelectedInstallation().getPath().getParent(), TinkerTime.SAFE_NAME);
	}
}
