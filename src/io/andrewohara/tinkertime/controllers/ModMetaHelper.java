package io.andrewohara.tinkertime.controllers;

import io.andrewohara.tinkertime.controllers.ModExceptions.ModNotDownloadedException;
import io.andrewohara.tinkertime.models.ConfigFactory;
import io.andrewohara.tinkertime.models.Mod;

import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipFile;

import com.google.inject.Inject;

public class ModMetaHelper {

	private final ConfigFactory configFactory;

	@Inject
	ModMetaHelper(ConfigFactory configFactory){
		this.configFactory = configFactory;
	}

	public boolean isDownloaded(Mod mod){
		Path zipPath = getZipPath(mod);
		return zipPath != null && zipPath.toFile().exists();
	}

	public Path getZipPath(Mod mod){
		if (mod.getNewestFileName() != null){
			String safePathFileName = mod.getNewestFileName().replaceAll(":", "").replaceAll("/", "");
			return configFactory.getConfig().getModsZipPath().resolve(safePathFileName);
		}
		return null;
	}

	public ZipFile getZipFile(Mod mod) throws ModNotDownloadedException {
		try {
			return new ZipFile(getZipPath(mod).toFile());
		} catch (NullPointerException | IOException e) {
			throw new ModNotDownloadedException(mod, e.toString());
		}
	}

	public boolean isEnabled(Mod mod){
		//TODO Implement
		return true;
	}
}
