package aohara.tinkertime.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

import aoahara.common.Listenable;
import aohara.tinkertime.config.Config;
import aohara.tinkertime.controllers.files.ConflictResolver;
import aohara.tinkertime.controllers.files.ConflictResolver.Resolution;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModApi;
import aohara.tinkertime.models.ModPage;
import aohara.tinkertime.models.ModStructure;
import aohara.tinkertime.models.ModStructure.Module;

public class ModManager extends Listenable<ModUpdateListener> {
	
	private final ModDownloadManager dm;
	private final Config config;
	private final ConflictResolver cr;
	
	public ModManager(
			ModStateManager sm, ModDownloadManager dm, Config config,
			ConflictResolver cr){
		this.dm = dm;
		this.config = config;
		this.cr = cr;
		addListener(sm);
	}
	
	// -- Listeners -----------------------
	
	private void notifyListeners(Mod mod, boolean deleted){
		for (ModUpdateListener l : getListeners()){
			l.modUpdated(mod, deleted);
		}
	}
	
	// -- Accessors ------------------------
	
	public boolean isDownloaded(ModApi mod){
		return config.getModZipPath(mod).toFile().exists();
	}
	
	public boolean isUpdateAvailable(Mod mod){
		return dm.isUpdateAvailable(mod);
	}
	
	// -- Modifiers ---------------------------------
	
	public Mod addNewMod(String url) throws CannotAddModException{
		return addNewMod(ModPage.createFromUrl(url));
	}
	
	public Mod addNewMod(ModPage modPage) throws CannotAddModException, CannotAddModException {
		Mod mod = new Mod(modPage);
		dm.downloadMod(mod);
		notifyListeners(mod, false);
		return mod;
	}
	
	public void enableMod(Mod mod)
		throws ModAlreadyEnabledException, ModNotDownloadedException,
		CannotEnableModException, CannotDisableModException
	{
		if (mod.isEnabled()){
			throw new ModAlreadyEnabledException();
		} else if (!isDownloaded(mod)){
			throw new ModNotDownloadedException();
		}
		
		ModStructure structure = new ModStructure(mod, config);
		for (Module module : structure.getModules()){
			
			File conflict = module.getConflict(config);
			if (conflict != null){
				// There is a conflict, so resolve it
				Resolution res = cr.getResolution(module, mod);
				if (res.equals(Resolution.Overwrite)){
					disableModule(conflict);
					enableModule(structure, module);
				} else if (res.equals(Resolution.Skip)){
					// Skip Module
				} else {
					throw new IllegalStateException("Uncaught Resolution");
				}
			} 
			// No Conflict, so just enable module
			else {
				enableModule(structure, module);
			}
		}
		
		mod.setEnabled(true);
		notifyListeners(mod, false);
	}
	
	public void disableMod(Mod mod)
			throws ModAlreadyDisabledException, CannotDisableModException {
		if (!mod.isEnabled()){
			throw new ModAlreadyDisabledException();
		}
		
		for (Module module : new ModStructure(mod, config).getModules()){
			if (cr.getDependentMods(module).size() == 1){
				disableModule(module);
			}
		}
		
		mod.setEnabled(false);
		notifyListeners(mod, false);
	}
	
	private void enableModule(ModStructure structure, Module module)
			throws CannotEnableModException {
		try {
			structure.getZipManager().unzipModule(
				module.getEntries(), config.getGameDataPath());
		} catch (IOException e) {
			throw new CannotEnableModException();
		}
	}
	
	private void disableModule(Module module) throws CannotDisableModException {
		Path path = config.getGameDataPath().resolve(module.getName());
		disableModule(path.toFile());
	}
	
	private void disableModule(File file) throws CannotDisableModException {
		try {
			FileUtils.deleteDirectory(file);
		} catch (IOException e) {
			throw new CannotDisableModException();
		}
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
		CannotDisableModException, CannotAddModException
	{
		tryDisableMod(mod);
		if (!dm.tryUpdateData(mod)){
			throw new ModAlreadyUpToDateException();
		}
		dm.downloadMod(mod);
		
		// Notify listeners of mod update
		for (ModUpdateListener l : getListeners()){
			l.modUpdated(mod, false);
		}
	}
	
	public void deleteMod(Mod mod) throws CannotDisableModException {
		tryDisableMod(mod);
		
		notifyListeners(mod, true);
		FileUtils.deleteQuietly(config.getModZipPath(mod).toFile());
	}
	
	// -- Exceptions -----------------------
	
	@SuppressWarnings("serial")
	public static class CannotAddModException extends Throwable {}
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
