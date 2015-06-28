package aohara.tinkertime.controllers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipFile;

import com.google.inject.Inject;

import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.controllers.ModExceptions.ModNotDownloadedException;
import aohara.tinkertime.models.Mod;

public class ModMetaHelper {
	
	private final TinkerConfig config;
	
	@Inject
	ModMetaHelper(TinkerConfig config){
		this.config = config;
	}
	
	public boolean isDownloaded(Mod mod){
		Path zipPath = getZipPath(mod);
		return zipPath != null && zipPath.toFile().exists();
	}
	
	public Path getZipPath(Mod mod){
		if (mod.newestFileName != null){
			String safePathFileName = mod.newestFileName.replaceAll(":", "").replaceAll("/", "");
			return config.getModsZipPath().resolve(safePathFileName);
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
}
