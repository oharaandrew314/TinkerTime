package io.andrewohara.tinkertime.models;

import io.andrewohara.tinkertime.models.mod.Mod;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "readmes")
public class Readme {

	@DatabaseField(generatedId=true)
	private int id;

	@DatabaseField(foreign = true, canBeNull=false)
	private Mod mod;

	@DatabaseField(dataType = DataType.LONG_STRING)
	private String text = "";

	Readme() { /* Required by ormlite */ }

	public Readme(Mod mod){
		this.mod = mod;
	}

	public void setText(String text) {
		this.text = text != null ? text : "";
	}

	public String getText(){
		return text;
	}
}
