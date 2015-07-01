package io.andrewohara.tinkertime.models;

import io.andrewohara.tinkertime.models.mod.Mod;

import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "modFiles")
public class ModFile {

	@DatabaseField(generatedId=true)
	private int id;

	@DatabaseField(foreign = true, canBeNull=false)
	private Mod mod;

	@DatabaseField(canBeNull=false)
	private String entryName, relDestPath;

	// Required by ormlite
	ModFile() { }

	public ModFile(Mod mod, String entryName, Path relPath){
		this.mod = mod;
		this.entryName = entryName.toString();
		this.relDestPath = relPath.toString();
	}

	public Path getDestPath(){
		return mod.getInstallation().getGameDataPath().resolve(relDestPath);
	}

	public ZipEntry getEntry(ZipFile zipFile){
		return zipFile.getEntry(entryName);
	}
}
