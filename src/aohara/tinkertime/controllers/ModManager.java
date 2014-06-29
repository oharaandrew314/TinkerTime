package aohara.tinkertime.controllers;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import aoahara.common.Listenable;
import aohara.tinkertime.config.Config;
import aohara.tinkertime.controllers.files.ConflictResolver;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModStructure;
import aohara.tinkertime.models.ModPage;
import aohara.tinkertime.models.ModStructure.Module;

public class ModManager extends Listenable<ModUpdateListener> {
	
	private final ModStateManager sm;
	
	public ModManager(ModStateManager sm){
		this.sm = sm;
		addListener(sm);
	}
	
	// -- State Methods ------------------------
	
	public static boolean isDownloaded(Mod mod){
		return new Config().getModZipPath(mod).toFile().exists();
	}
	
	private void notifyListeners(Mod mod){
		for (ModUpdateListener l : getListeners()){
			l.modUpdated(mod);
		}
	}
	
	// -- Modifiers ---------------------------------
	
	public void enableMod(Mod mod, Config config, ConflictResolver cr)
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
		
		ModStructure structure = new ModStructure(mod);
		for (Module module : structure.getModules()){
			if (module.isEnabled()){
				// TODO: Resolve Conflict
			} else {
				structure.getZipManager().unzipModule(
					module, config.getGameDataPath());
			}
		}
		
		mod.setEnabled(true);
		notifyListeners(mod);
		System.out.println("Enabled " + mod.getName());
	}
	
	public void disableMod(Mod mod) throws ModAlreadyDisabledException, CannotDisableModException {
		if (!mod.isEnabled()){
			throw new ModAlreadyDisabledException();
		}
		
		System.out.println("Disabling " + mod.getName());
		Config config = new Config();
		
		try {
			for (Module module : new ModStructure(mod).getModules()){
				if (getDependentMods(module).size() == 1){
					FileUtils.deleteDirectory(
						config.getGameDataPath().resolve(module.getName())
						.toFile()
					);
				}
			}
		} catch (IOException e) {
			throw new CannotDisableModException();
		}
		
		mod.setEnabled(false);
		notifyListeners(mod);
		System.out.println("Disabled " + mod.getName());
	}
	
	private Set<Mod> getDependentMods(Module module){
		Set<Mod> mods = new HashSet<Mod>();
		
		for (Entry<Mod, ModStructure> entry : sm.getModStructures().entrySet()){
			if (entry.getValue().usesModule(module)){
				mods.add(entry.getKey());
			}
		}
		
		return mods;
	}
	
	public Mod addNewMod(
		ModPage modPage,
		ModDownloadManager downloadManager,
		ModUpdateListener updateListener
	) {
		System.out.println("Adding " + modPage.getName());
		Mod mod = new Mod(modPage);
		updateListener.modUpdated(mod);
		downloadManager.downloadMod(mod);
		notifyListeners(mod);
		System.out.println("Added mod: " + mod.getName());
		return mod;
	}
	
	public void deleteMod(Mod mod) throws CannotDisableModException {
		try {
			disableMod(mod);
		} catch (ModAlreadyDisabledException e) {
			// Do Nothing
		}
		
		System.out.println("Deleting " + mod.getName());
		notifyListeners(mod); // FIXME this will not work
		FileUtils.deleteQuietly(new Config().getModZipPath(mod).toFile());
		System.out.println("Deleted " + mod.getName());
	}
	
	public boolean isUpdateAvailable(Mod mod){
		try {
			ModPage remoteMod = new ModPage(mod.getPageUrl());
			return mod.isNewer(remoteMod);
		} catch (IOException e) {
			return false;
		}
	}
	
	public void updateMod(
		Mod mod, ModDownloadManager downloadManager
	) throws ModUpdateFailedException, ModAlreadyUpToDateException, CannotDisableModException {
		System.out.println("Updating " + mod.getName());
		
		try {
			disableMod(mod);
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
			
			// Notify listeners of mod update
			for (ModUpdateListener l : getListeners()){
				l.modUpdated(mod);
			}

			downloadManager.downloadMod(mod);
		} catch (IOException e) {
			throw new ModUpdateFailedException();
		}
	}
	
	// -- Exceptions -----------------------
	
	@SuppressWarnings("serial")
	public static class ModAlreadyEnabledException extends Throwable {}
	@SuppressWarnings("serial")
	public static class ModAlreadyDisabledException extends Throwable {}
	@SuppressWarnings("serial")
	public static class ModNotDownloadedException extends Throwable {}
	@SuppressWarnings("serial")
	public static class CannotDisableModException extends Throwable {}
	@SuppressWarnings("serial")
	public static class ModUpdateFailedException extends Exception {}
	@SuppressWarnings("serial")
	public static class ModAlreadyUpToDateException extends Exception {}

}
