package io.andrewohara.tinkertime.controllers;

import io.andrewohara.tinkertime.controllers.ModManager.ModUpdateFailedException;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.sql.SQLException;

import org.apache.commons.io.FilenameUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;

public class ImportController {

	private final ModManager modManager;
	private final ModLoader modLoader;

	@Inject
	ImportController(ModManager modManager, ModLoader modLoader){
		this.modManager = modManager;
		this.modLoader = modLoader;
	}

	public int importMods(Path sourcePath) throws ModImportException{
		try {
			String extension = FilenameUtils.getExtension(sourcePath.toString());
			if (extension.equalsIgnoreCase("json")){
				return importJsonList(sourcePath);
			} else if (extension.equalsIgnoreCase("txt")){
				return importModsList(sourcePath);
			} else {
				throw new ModImportException("Cannot import.  Invalid file extension.");
			}
		} catch (JsonSyntaxException | JsonIOException | IOException e){
			throw new ModImportException("Cannot import", e);
		}
	}

	private int importModsList(Path sourcePath) throws FileNotFoundException, IOException {
		int imported = 0;
		try (BufferedReader reader = new BufferedReader(new FileReader(sourcePath.toFile()))){
			while (reader.ready()){
				String url = reader.readLine();
				if (!url.trim().isEmpty()){
					try {
						if (modManager.downloadNewMod(new URL(url))){
							imported++;
						}
					} catch (SQLException | ModUpdateFailedException e) {
						e.printStackTrace();  // TODO send all exceptions to the user somehow
					}
				}
			}
		}
		return imported;
	}

	private int importJsonList(Path sourcePath) throws JsonIOException, JsonSyntaxException, FileNotFoundException, MalformedURLException {
		int imported = 0;
		JsonArray mods = new JsonParser().parse(new FileReader(sourcePath.toFile())).getAsJsonArray();
		for (JsonElement modEle : mods){
			String url = modEle.getAsJsonObject().get("pageUrl").getAsString();
			try {
				if (modManager.downloadNewMod(new URL(url))){
					imported++;
				}
			} catch (SQLException | ModUpdateFailedException e) {
				e.printStackTrace();  // TODO send all exceptions to the user somehow
			}
		}
		return imported;
	}

	public int exportMods(Path destPath, boolean enabledOnly) throws IOException{
		int exported = 0;
		try (FileWriter writer = new FileWriter(destPath.toFile())){
			for (Mod mod : modLoader.getMods()){
				if (!enabledOnly || mod.isEnabled()){
					writer.write(mod.getUrl().toString());
					writer.write("\n");
					exported++;
				}
			}
		}
		return exported;
	}

	@SuppressWarnings("serial")
	public static class ModImportException extends Exception {

		public ModImportException(String message, Exception cause){
			super(message, cause);
		}

		public ModImportException(String message){
			super(message);
		}
	}
}
