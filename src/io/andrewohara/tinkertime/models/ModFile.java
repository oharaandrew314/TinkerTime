package io.andrewohara.tinkertime.models;

import io.andrewohara.tinkertime.models.mod.Mod;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "modFiles")
public class ModFile extends BaseDaoEnabled<ModFile, Integer>{

	@DatabaseField(generatedId=true)
	private int id;

	@DatabaseField(foreign = true, canBeNull=false)
	private Mod mod;

	@DatabaseField(canBeNull=false)
	private String entryName, relDestPath;

	// Required by ormlite
	ModFile() {	}

	public ModFile(Mod mod, String entryName, Path relPath, Dao<ModFile, Integer> dao) throws SQLException{
		this.mod = mod;
		this.entryName = entryName.toString();
		this.relDestPath = relPath.toString();

		if (id == 0 && dao != null){
			setDao(dao);
			create();
		}
	}

	public Path getRelDestPath(){
		return Paths.get(relDestPath);
	}

	public Path getDestPath(){
		return mod.getInstallation().getGameDataPath().resolve(relDestPath);
	}

	public ZipEntry getEntry(ZipFile zipFile){
		return zipFile.getEntry(entryName);
	}
}
