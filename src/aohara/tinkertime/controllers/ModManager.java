package aohara.tinkertime.controllers;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

import aohara.tinkertime.config.Config;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModApi;

public class ModManager {
	
	// -- Path Methods ----------------------
	
	private Path kerbalPath(){
		return new Config().getKerbalPath();
	}
	
	private Path modZipPath(ModApi mod){
		return new Config().getModsPath().resolve(mod.getNewestFile());
	}
	
	// -- State Methods ------------------------
	
	public boolean isDownloaded(Mod mod){
		return modZipPath(mod).toFile().exists();
	}
	
	// -- Modifiers ---------------------------------
	
	public void enableMod(Mod mod)
		throws ModAlreadyEnabledException,
		ModNotDownloadedException,
		IOException
	{
		if (mod.isEnabled()){
			throw new ModAlreadyEnabledException();
		} else if (!isDownloaded(mod)){
			throw new ModNotDownloadedException();
		}
		
		System.out.println("Enabling " + mod.getName());
		ZipManager.unzipFile(modZipPath(mod), kerbalPath());
		mod.setEnabled(true);
		System.out.println("Enabled " + mod.getName());
	}
	
	public void disableMod(Mod mod) throws ModAlreadyDisabledException {
		if (!mod.isEnabled()){
			throw new ModAlreadyDisabledException();
		}
		
		System.out.println("Disabling " + mod.getName());
		ZipManager.deleteZipFiles(modZipPath(mod), kerbalPath());
		mod.setEnabled(false);
		System.out.println("Disabled " + mod.getName());
	}
	
	public void downloadMod(ModApi mod) throws ModNotDownloadedException, ModAlreadyDownlodedException {
		if (modZipPath(mod).toFile().exists()){
			throw new ModAlreadyDownlodedException();
		}
		
		System.out.println("Downloading " + mod.getNewestFile());
		try {
			FileUtils.copyURLToFile(
				mod.getDownloadLink(),
				modZipPath(mod).toFile()
			);
			System.out.println("Finished downloading " + mod.getNewestFile());
		} catch (IOException e) {
			throw new ModNotDownloadedException();
		}
	}
	
	public void deleteMod(Mod mod) {
		// Try to disable mod
		try {
			disableMod(mod);
		} catch (ModAlreadyDisabledException e) {
			// Do Nothing
		}
		
		System.out.println("Deleting " + mod.getName());
		FileUtils.deleteQuietly(modZipPath(mod).toFile());
		System.out.println("Deleted " + mod.getName());
	}
	
	// -- Exceptions -----------------------
	
	@SuppressWarnings("serial")
	public class ModAlreadyEnabledException extends Throwable {}
	@SuppressWarnings("serial")
	public class ModAlreadyDisabledException extends Throwable {}
	@SuppressWarnings("serial")
	public class ModNotDownloadedException extends Throwable {}
	@SuppressWarnings("serial")
	public class ModAlreadyDownlodedException extends Throwable {}

}
