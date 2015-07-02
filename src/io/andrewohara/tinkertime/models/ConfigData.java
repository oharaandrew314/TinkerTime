package io.andrewohara.tinkertime.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "config")
public class ConfigData {

	public static final Integer CONFIG_ID = 1;

	public static final int NUM_CONCURRENT_DOWNLOADS = 4;

	/////////////
	// Columns //
	/////////////

	@DatabaseField(id=true)
	private int id = CONFIG_ID;

	@DatabaseField(canBeNull = false)
	private boolean checkForAppUpdatesOnStartup = true, checkForModUpdatesOnStartup = true;

	@DatabaseField(foreign = true, foreignAutoRefresh=true)
	private Installation selectedInstallation;

	@DatabaseField
	private String launchArguments;

	///////////////
	// Interface //
	///////////////

	public boolean isCheckForAppUpdatesOnStartup(){
		return checkForAppUpdatesOnStartup;
	}
	public void setCheckForAppUpdatesOnStartup(boolean check){
		this.checkForAppUpdatesOnStartup = check;
	}

	public boolean isCheckForModUpdatesOnStartup(){
		return checkForModUpdatesOnStartup;
	}
	public void setCheckForModUpdatesOnStartup(boolean check){
		this.checkForModUpdatesOnStartup = check;
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

}
