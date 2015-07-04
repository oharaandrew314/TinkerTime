package io.andrewohara.tinkertime.models;

import io.andrewohara.tinkertime.TinkerTimeLauncher;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "installations")
public class Installation extends BaseDaoEnabled<Installation, Integer>{

	@DatabaseField(generatedId=true)
	private int id;

	@DatabaseField(canBeNull=false)
	private String name, path;

	@ForeignCollectionField(eager = true)
	private Collection<Mod> mods = new LinkedList<>();

	Installation() { /* Required by ormlite */ }

	public Installation(String name, Path path, Dao<Installation, Integer> dao) throws InvalidGameDataPathException, SQLException{
		this.name = name;

		if (!path.endsWith("GameData")) {
			throw new InvalidGameDataPathException(path, "Must be a GameData Path");
		} else if (!path.toFile().isDirectory()){
			throw new InvalidGameDataPathException(path, "Must be an existing directory");
		}
		this.path = path.toString();

		setDao(dao);
		create();
	}

	/////////////
	// Setters //
	/////////////

	public void rename(String name) throws SQLException{
		this.name = name;
		update();
	}

	public void addMod(Mod mod) throws SQLException{
		if (!mods.contains(mod)){
			mods.add(mod);
			update();
		}
	}

	public void removeMod(Mod mod) throws SQLException{
		mods.remove(mod);
		update();
	}

	/////////////
	// Getters //
	/////////////

	public String getName(){
		return name;
	}

	public Path getGameDataPath(){
		return Paths.get(path);
	}

	public Collection<Mod> getMods(){
		return new LinkedList<>(mods);
	}

	public Path getModZipsPath(){
		return getGameDataPath().getParent().resolve(TinkerTimeLauncher.SAFE_NAME + "-ModCache");
	}

	/////////
	// Dao //
	/////////

	@Override
	public int delete() throws SQLException {
		for (Mod mod : getMods()){
			mod.delete();
		}
		return super.delete();
	}

	////////////
	// Object //
	////////////

	@Override
	public String toString(){
		return getName();
	}

	@Override
	public boolean equals(Object o){
		return o instanceof Installation && ((Installation)o).id == id;
	}

	////////////////
	// Exceptions //
	////////////////

	public static class InvalidGameDataPathException extends Exception {

		public InvalidGameDataPathException(Path path, String reason){
			super(String.format("The GameDataPath: %s, is invalid: %s", path, reason));
		}
	}
}
