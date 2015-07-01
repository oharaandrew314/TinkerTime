package io.andrewohara.tinkertime.models;

import io.andrewohara.tinkertime.models.mod.Mod;

import java.nio.file.Path;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "modFiles")
public class ModFile {

	@DatabaseField(generatedId=true)
	private int id;

	@DatabaseField(foreign = true, canBeNull=false)
	private Mod mod;

	@DatabaseField(canBeNull=false)
	private String srcPath, destPath;

	@DatabaseField(canBeNull=false)
	private boolean inZip;

	// Required by ormlite
	ModFile() { }

	public ModFile(Mod mod, Path srcPath, Path destPath){
		this(mod, srcPath, destPath, true);
	}

	public ModFile(Mod mod, Path srcPath, Path destPath, boolean inZip){
		this.mod = mod;
		this.srcPath = srcPath.toString();
		this.destPath = destPath.toString();
		this.inZip = inZip;
	}
}
