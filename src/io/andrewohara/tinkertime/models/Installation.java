package io.andrewohara.tinkertime.models;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "installations")
public class Installation {

	@DatabaseField(generatedId=true)
	private int id;

	@DatabaseField
	private String name, path;

	@ForeignCollectionField
	private Collection<Mod> mods;

	// Required by ormlite
	Installation() { }

	public Installation(String name, Path path) throws InvalidGameDataPathException{
		rename(name);
		setPath(path);
	}

	public String getName(){
		return name;
	}
	public void rename(String name){
		this.name = name;
	}

	public Path getPath(){
		return Paths.get(path);
	}
	public void setPath(Path path) throws InvalidGameDataPathException {
		if (!path.endsWith("GameData")) {
			throw new InvalidGameDataPathException(path, "Must be a GameData Path");
		} else if (!path.toFile().isDirectory()){
			throw new InvalidGameDataPathException(path, "Must be an existing directory");
		}
		this.path = path.toString();
	}

	public List<Mod> getMods(){
		return new LinkedList<>(mods);
	}

	public void unlinkMod(Mod mod){
		mods.remove(mod);
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
