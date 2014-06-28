package aohara.tinkertime.controllers;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

import aohara.tinkertime.config.Config;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModApi;
import aohara.tinkertime.models.ModPage;

public class ModManager {
	
	// -- Path Methods ----------------------
	
	private static Path kerbalPath(){
		return new Config().getKerbalPath();
	}
	
	public static Path modZipPath(ModApi mod){
		return new Config().getModsPath().resolve(mod.getNewestFile());
	}
	
	// -- State Methods ------------------------
	
	public static boolean isDownloaded(Mod mod){
		return modZipPath(mod).toFile().exists();
	}
	
	// -- Modifiers ---------------------------------
	
	public static void enableMod(Mod mod)
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
	
	public static void disableMod(Mod mod) throws ModAlreadyDisabledException {
		if (!mod.isEnabled()){
			throw new ModAlreadyDisabledException();
		}
		
		System.out.println("Disabling " + mod.getName());
		ZipManager.deleteZipFiles(modZipPath(mod), kerbalPath());
		mod.setEnabled(false);
		System.out.println("Disabled " + mod.getName());
	}
	
	public static Mod addNewMod(
		ModPage modPage,
		ModDownloadManager downloadManager,
		ModUpdateListener updateListener
	) {
		System.out.println("Adding " + modPage.getName());
		Mod mod = new Mod(modPage);
		updateListener.modUpdated(mod);
		downloadManager.downloadMod(mod);
		System.out.println("Added mod: " + mod.getName());
		return mod;
	}
	
	public static void deleteMod(Mod mod) {
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
	public static class ModAlreadyEnabledException extends Throwable {}
	@SuppressWarnings("serial")
	public static class ModAlreadyDisabledException extends Throwable {}
	@SuppressWarnings("serial")
	public static class ModNotDownloadedException extends Throwable {}

}
