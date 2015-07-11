package io.andrewohara.tinkertime.models.mod;

import io.andrewohara.common.version.Version;

import java.util.Date;

public class ModUpdateData {

	public final String name, creator, kspVersion;
	public final Date updatedOn;
	public final Version modVersion;

	public ModUpdateData(String name, String creator, Date updatedOn, Version modVersion, String kspVersion){
		this.name = name;
		this.creator = creator;
		this.updatedOn = updatedOn;
		this.modVersion = modVersion;
		this.kspVersion = kspVersion;
	}
}
