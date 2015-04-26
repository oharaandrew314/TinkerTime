package aohara.tinkertime.workflows;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Date;
import java.util.Set;

import aohara.common.views.Dialogs;
import aohara.common.workflows.tasks.WorkflowBuilder;
import aohara.tinkertime.ModManager.ModNotDownloadedException;
import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.resources.ModLoader;
import aohara.tinkertime.workflows.DownloadModAssetTask.ModDownloadType;

import com.github.zafarkhaja.semver.Version;

public class ModWorkflowBuilder extends WorkflowBuilder {
	
	public ModWorkflowBuilder(Mod context) {
		super(context);
	}
	
	/**
	 * Notifies the listeners if an update is available for the given file
	 */
	public void checkForUpdates(Crawler<?> crawler, Version currentVersion, Date lastUpdatedOn, CheckForUpdateTask.OnUpdateAvailable onUpdateAvailable) {
		addTask(new CheckForUpdateTask(crawler, currentVersion, lastUpdatedOn, onUpdateAvailable));
	}
	
	public void downloadNewMod(Crawler<?> crawler, TinkerConfig config, ModLoader modLoader) {
		// Create Placeholder Mod
		try {
			Mod placeholder = Mod.newTempMod(crawler.getApiUrl());
			updateContext(placeholder);
			addTask(new SaveModTask.FromMod(modLoader, placeholder));
		} catch (MalformedURLException e) {
			Dialogs.errorDialog(null, e);
		}
		
		// Download Mod
		updateMod(crawler, config, modLoader);
	}
	
	/**
	 * Downloads the latest version of the mod referenced by the URL.
	 */
	public void updateMod(Crawler<?> crawler, TinkerConfig config, ModLoader modLoader) {
		addTask(new RunCrawlerTask(crawler));
		
		addTask(new DownloadModAssetTask(crawler, config, modLoader, ModDownloadType.File));
		addTask(new DownloadModAssetTask(crawler, config, modLoader, ModDownloadType.Image));
		addTask(new SaveModTask.FromCrawler(modLoader, crawler));
	}
	
	public void addLocalMod(Path zipPath, ModLoader modLoader){
		// Create Placeholder Mod
		Mod newMod = Mod.newTempMod(zipPath);
		updateContext(newMod);
		addTask(new SaveModTask.FromMod(modLoader, newMod));
		
		// Add Mod
		copy(zipPath, modLoader.getZipPath(newMod));
		addTask(new SaveModTask.FromMod(modLoader, newMod));
	}
	
	/**
	 * Delete the mod's zip file, but do not mark the mod as deleted
	 * @param mod
	 * @param config
	 */
	public void deleteModZip(final Mod mod, final ModLoader modLoader){
		delete(modLoader.getZipPath(mod));
	}
	
	/**
	 * Fully delete the mod and mark it as deleted.
	 * @param mod
	 * @param config
	 * @param modLoader
	 */
	public void deleteMod(Mod mod, TinkerConfig config, ModLoader modLoader) {
		// Try to disable the mod first
		try {
			if (modLoader.isEnabled(mod)){
				disableMod(mod, modLoader);
			}
		} catch (ModNotDownloadedException e) {
			// Do nothing
		}
		
		deleteModZip(mod, modLoader);
		delete(mod.getCachedImagePath(config));
	}
	
	public void disableMod(Mod mod, ModLoader modLoader) throws ModNotDownloadedException{
		Set<Path> fileDestPaths = modLoader.getModFileDestPaths(mod);
		
		// Check if any files for this mod are dependencies of other mods.
		// All files which are a dependency will not be deleted
		for (Mod otherMod : modLoader.getMods()){
			try{
				if (!otherMod.equals(mod) && modLoader.isEnabled(otherMod)){
					fileDestPaths.removeAll(modLoader.getModFileDestPaths(otherMod));
				}
			} catch (ModNotDownloadedException e){
				// Ignore
			}
		}
		
		// Delete the files that do not conflict with other enabled mods
		for (Path filePath : fileDestPaths){
			delete(filePath);
		}
		
		addTask(new SaveModTask.FromMod(modLoader, mod));
	}
	
	public void enableMod(Mod mod, ModLoader modLoader, TinkerConfig config) throws ModNotDownloadedException {
		try {
			Path zipPath = modLoader.getZipPath(mod);
			if (zipPath == null){
				throw new ModNotDownloadedException(mod, "mod has no zip path");
			}
			
			if (zipPath.toString().endsWith(".zip")){
				// If mod is a zip file, unzip it
				unzip(zipPath, modLoader.getStructure(mod).getZipEntries(), config.getGameDataPath());
			} else {
				// Otherwise, it is just a file.  Copy it
				copy(zipPath, config.getGameDataPath());
			}
			
			addTask(new SaveModTask.FromMod(modLoader, mod));
			
		} catch (IOException e) {
			throw new ModNotDownloadedException(mod, e.toString());
		}
	}
}
