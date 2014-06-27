package aohara.tinkertime.controllers;

import java.io.IOException;

import aohara.tinkertime.controllers.ModManager.ModAlreadyDisabledException;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModPage;

public class UpdateManager {
	
	public static boolean isUpdateAvailable(Mod mod){
		try {
			ModPage remoteMod = new ModPage(mod.getPageUrl());
			return mod.isNewer(remoteMod);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void updateMod(
		Mod mod, ModDownloadManager downloadManager
	) throws ModUpdateFailedException, ModAlreadyUpToDateException {
		System.out.println("Updating " + mod.getName());
		
		// Try to disable existing mod
		try {
			ModManager.disableMod(mod);
		} catch (ModAlreadyDisabledException e) {
			// Do nothing
		}
		
		// Try to update mod
		try {
			ModPage remotePage = new ModPage(mod.getPageUrl());
			
			// If mod is up to date, throw exception
			if (!mod.isNewer(remotePage)){
				throw new ModAlreadyUpToDateException();
			}
			mod.updateModData(remotePage);
			downloadManager.downloadMod(mod);
		} catch (IOException e) {
			throw new ModUpdateFailedException();
		}
	}
	
	@SuppressWarnings("serial")
	public static class ModUpdateFailedException extends Exception {}
	@SuppressWarnings("serial")
	public static class ModAlreadyUpToDateException extends Exception {}
}
