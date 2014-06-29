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
import aohara.tinkertime.models.ModApi;
import aohara.tinkertime.models.ModStructure;
import aohara.tinkertime.models.ModPage;
import aohara.tinkertime.models.ModStructure.Module;

public class ModManager extends Listenable<ModUpdateListener> {
	
	private final ModStateManager sm;
	private final ModDownloadManager dm;
	private final Config config;
	
	public ModManager(ModStateManager sm, ModDownloadManager dm, Config config){
		this.sm = sm;
		this.dm = dm;
		this.config = config;
		addListener(sm);
	}
	
	// -- Listeners -----------------------
	
	private void notifyListeners(Mod mod){
		for (ModUpdateListener l : getListeners()){
			l.modUpdated(mod);
		}
	}
	
	// -- Accessors ------------------------
	
	public boolean isDownloaded(ModApi mod){
		return config.getModZipPath(mod).toFile().exists();
	}
	
	public boolean isUpdateAvailable(Mod mod){
		return dm.isUpdateAvailable(mod);
	}
	
	public Set<Mod> getDependentMods(Module module){
		Set<Mod> mods = new HashSet<Mod>();
		
		for (Entry<Mod, ModStructure> entry : sm.getModStructures(config).entrySet()){
			if (entry.getValue().usesModule(module)){
				mods.add(entry.getKey());
			}
		}
		return mods;
	}
	
	// -- Modifiers ---------------------------------
	
	public Mod addNewMod(ModPage modPage) {
		Mod mod = new Mod(modPage);
		dm.downloadMod(mod);
		notifyListeners(mod);
		return mod;
	}
	
	public void enableMod(Mod mod, ConflictResolver cr)
		throws ModAlreadyEnabledException, ModNotDownloadedException,
		CannotEnableModException
	{
		if (mod.isEnabled()){
			throw new ModAlreadyEnabledException();
		} else if (!isDownloaded(mod)){
			throw new ModNotDownloadedException();
		}
		
		ModStructure structure = new ModStructure(mod, config);
		for (Module module : structure.getModules()){
			if (module.isEnabled(config)){
				// TODO: Resolve Conflict
			} else {
				try {
					structure.getZipManager().unzipModule(
						module.getEntries(), config.getGameDataPath());
				} catch (IOException e) {
					throw new CannotEnableModException();
				}
			}
		}
		
		mod.setEnabled(true);
		notifyListeners(mod);
	}
	
	public void disableMod(Mod mod) throws ModAlreadyDisabledException, CannotDisableModException {
		if (!mod.isEnabled()){
			throw new ModAlreadyDisabledException();
		}
		
		try {
			for (Module module : new ModStructure(mod, config).getModules()){
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
	}
	
	private void tryDisableMod(Mod mod) throws CannotDisableModException {
		try {
			disableMod(mod);
		} catch (ModAlreadyDisabledException e) {
			// Do Nothing
		}
	}
	
	public void updateMod(Mod mod) 
		throws ModUpdateFailedException, ModAlreadyUpToDateException,
		CannotDisableModException
	{
		tryDisableMod(mod);
		dm.tryUpdateData(mod); // Throws Exception if failure
		dm.downloadMod(mod);
		
		// Notify listeners of mod update
		for (ModUpdateListener l : getListeners()){
			l.modUpdated(mod);
		}
	}
	
	public void deleteMod(Mod mod) throws CannotDisableModException {
		tryDisableMod(mod);
		
		notifyListeners(mod); // FIXME this will not work
		FileUtils.deleteQuietly(config.getModZipPath(mod).toFile());
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
	public static class CannotEnableModException extends Throwable {}
	@SuppressWarnings("serial")
	public static class ModUpdateFailedException extends Exception {}
	@SuppressWarnings("serial")
	public static class ModAlreadyUpToDateException extends Exception {}

}
