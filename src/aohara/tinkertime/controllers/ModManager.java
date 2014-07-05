package aohara.tinkertime.controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

import aohara.common.Listenable;
import aohara.common.executors.Downloader;
import aohara.tinkertime.config.Config;
import aohara.tinkertime.controllers.files.ConflictResolver;
import aohara.tinkertime.controllers.files.ConflictResolver.Resolution;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModApi;
import aohara.tinkertime.models.ModPage;
import aohara.tinkertime.models.ModStructure;
import aohara.tinkertime.models.ModStructure.Module;

public class ModManager extends Listenable<ModUpdateListener> {
	
	public static final int NUM_CONCURRENT_DOWNLOADS = 4;
	
	private final ModPageDownloader dm;
	private final Downloader downloader;
	private final Config config;
	private final ConflictResolver cr;
	
	public ModManager(
			ModStateManager sm, ModPageDownloader dm, Config config,
			ConflictResolver cr, Downloader downloader){
		this.dm = dm;
		this.config = config;
		this.cr = cr;
		this.downloader = downloader;
		addListener(sm);
	}
	
	// -- Listeners -----------------------
	
	public void notifyModUpdated(Mod mod, boolean deleted){
		for (ModUpdateListener l : getListeners()){
			l.modUpdated(mod, deleted);
		}
	}
	
	// -- Accessors ------------------------
	
	public static boolean isDownloaded(ModApi mod, Config config){
		return config.getModZipPath(mod).toFile().exists();
	}
	
	public boolean isDownloaded(ModApi mod){
		return isDownloaded(mod, config);
	}
	
	private boolean isUpdateAvailable(Mod mod){
		return dm.isUpdateAvailable(mod);
	}
	
	// -- Modifiers ---------------------------------
	
	public Mod addNewMod(String url) throws CannotAddModException{
		try {
			return addNewMod(ModPage.createFromUrl(new URL(url)));
		} catch (MalformedURLException e) {
			throw new CannotAddModException();
		}
	}
	
	public Mod addNewMod(ModPage modPage) throws CannotAddModException, CannotAddModException {
		Mod mod = new Mod(modPage);
		downloadMod(mod);
		notifyModUpdated(mod, false);
		return mod;
	}
	
	private void downloadMod(Mod mod){
		downloader.download(mod.getDownloadLink(), config.getModZipPath(mod));
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
		notifyModUpdated(mod, false);
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
		notifyModUpdated(mod, false);
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
		if (isUpdateAvailable(mod)){
			tryDisableMod(mod);
			dm.tryUpdateData(mod);
			downloadMod(mod);
		} else {
			throw new ModAlreadyUpToDateException();	
		}
		
		notifyModUpdated(mod, false);
	}
	
	public void deleteMod(Mod mod) throws CannotDisableModException {
		tryDisableMod(mod);
		
		notifyModUpdated(mod, true);
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
