package io.andrewohara.tinkertime.models;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "config")
public class ConfigData extends BaseDaoEnabled<ConfigData, Integer> {

	public static final int NUM_CONCURRENT_DOWNLOADS = 4;

	/////////////
	// Columns //
	/////////////

	ConfigData() { /* Used by ormlite */ }

	public ConfigData(Dao<ConfigData, Integer> dao) throws SQLException{
		setDao(dao);
		create();
	}

	@DatabaseField(id=true)
	private int id;

	@DatabaseField(canBeNull = false)
	private boolean checkForAppUpdatesOnStartup = true, checkForModUpdatesOnStartup = true;

	@DatabaseField(foreign = true, foreignAutoRefresh=true)
	private Installation selectedInstallation;

	@DatabaseField
	private String launchArguments;

	/////////////
	// Setters //
	/////////////

	public void setLaunchArguments(String args) throws SQLException{
		this.launchArguments = args;
		update();
	}

	public void setCheckForAppUpdatesOnStartup(boolean check) throws SQLException{
		this.checkForAppUpdatesOnStartup = check;
		update();
	}

	public void setCheckForModUpdatesOnStartup(boolean check) throws SQLException{
		this.checkForModUpdatesOnStartup = check;
		update();
	}

	public void setSelectedInstallation(Installation newInstallation) throws SQLException{
		this.selectedInstallation = newInstallation;
		update();
	}

	/////////////
	// Getters //
	/////////////

	public boolean isCheckForAppUpdatesOnStartup(){
		return checkForAppUpdatesOnStartup;
	}

	public boolean isCheckForModUpdatesOnStartup(){
		return checkForModUpdatesOnStartup;
	}

	public Installation getSelectedInstallation() {
		return selectedInstallation;
	}

	public String getLaunchArguments(){
		return launchArguments;
	}

}
