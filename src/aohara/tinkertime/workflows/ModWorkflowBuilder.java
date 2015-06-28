package aohara.tinkertime.workflows;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Set;

import com.google.inject.Inject;

import aohara.common.workflows.tasks.WorkflowBuilder;
import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.controllers.ModExceptions.ModNotDownloadedException;
import aohara.tinkertime.controllers.ModMetaHelper;
import aohara.tinkertime.controllers.ModUpdateCoordinator;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.CrawlerFactory;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.resources.ModMetaLoader;
import aohara.tinkertime.workflows.tasks.CheckForUpdateTask;
import aohara.tinkertime.workflows.tasks.DownloadModAssetTask;
import aohara.tinkertime.workflows.tasks.DownloadModInBrowserTask;
import aohara.tinkertime.workflows.tasks.MarkModUpdatedTask;
import aohara.tinkertime.workflows.tasks.RemoveModTask;
import aohara.tinkertime.workflows.tasks.RunCrawlerTask;
import aohara.tinkertime.workflows.tasks.SaveModTask;
import aohara.tinkertime.workflows.tasks.DownloadModAssetTask.ModDownloadType;

public class ModWorkflowBuilder extends WorkflowBuilder {
	
	private final TinkerConfig config;
	private final CrawlerFactory crawlerService;
	private final ModMetaHelper modMetaHelper;
	private final ModUpdateCoordinator updateCoordinator;
	private final ModMetaLoader modLoader;
	
	private Crawler<?> cachedCrawler;
	
	@Inject
	public ModWorkflowBuilder(TinkerConfig config, CrawlerFactory crawlerService, ModUpdateCoordinator updateCoordinator, ModMetaHelper modMetaHelper, ModMetaLoader modLoader) {
		this.config = config;
		this.crawlerService = crawlerService;
		this.updateCoordinator = updateCoordinator;
		this.modMetaHelper = modMetaHelper;
		this.modLoader = modLoader;
	}
	
	private Crawler<?> getCrawler(Mod mod) throws UnsupportedHostException{
		return (cachedCrawler != null) ? cachedCrawler : (cachedCrawler = crawlerService.getCrawler(mod.pageUrl));
	}
	
	/**
	 * Notifies the listeners if an update is available for the given file
	 * @throws UnsupportedHostException 
	 */
	public void checkForUpdates(Mod mod, boolean markIfAvailable) throws UnsupportedHostException {
		addTask(new CheckForUpdateTask(getCrawler(mod), mod.getVersion(), mod.updatedOn));
		if (markIfAvailable){
			addTask(new MarkModUpdatedTask(updateCoordinator, mod));
		}
	}
	
	public Mod downloadNewMod(URL url) throws UnsupportedHostException, MalformedURLException {		
		Mod tempMod = Mod.newTempMod(crawlerService.getCrawler(url));
		addTask(new SaveModTask.FromMod(updateCoordinator, tempMod));  // Create Placeholder Mod
		downloadMod(tempMod);
		return tempMod;
	}
	
	/**
	 * Downloads the latest version of the mod referenced by the URL.
	 * @throws UnsupportedHostException 
	 */
	public void updateMod(Mod mod, boolean forceUpdate) throws UnsupportedHostException {
		// Cleanup operations prior to update
		if (modMetaHelper.isDownloaded(mod)){
			if (!forceUpdate){
				checkForUpdates(mod, true);
			}
			
			// Disable Mod if it is enabled
			try {
				if (modLoader.isEnabled(mod)){
					disableMod(mod);
				}
			} catch (ModNotDownloadedException e) {
				// Do Nothing
			}
			
			addTask(new RunCrawlerTask(getCrawler(mod)));  // Get user to select asset before deleting
			deleteModZip(mod);
		}
		
		downloadMod(mod);
	}
	
	private void downloadMod(Mod mod) throws UnsupportedHostException{
		Crawler<?> crawler = getCrawler(mod);
		addTask(new RunCrawlerTask(crawler));  // prefetch metadata
		addTask(new DownloadModAssetTask(crawler, config, modMetaHelper, ModDownloadType.File));
		addTask(new DownloadModAssetTask(crawler, config, modMetaHelper, ModDownloadType.Image));
		addTask(new SaveModTask.FromCrawler(updateCoordinator, crawler));
	}
	
	public void downloadModInBrowser(Mod mod) throws UnsupportedHostException{
		addTask(new DownloadModInBrowserTask(getCrawler(mod), mod.getVersion()));
	}
	
	public Mod addLocalMod(Path zipPath){
		Mod tempMod = Mod.newTempMod(zipPath);
		
		// Create Placeholder Mod
		addTask(new SaveModTask.FromMod(updateCoordinator, tempMod));
		
		// Add Mod
		copy(zipPath, modMetaHelper.getZipPath(tempMod));
		addTask(new SaveModTask.FromMod(updateCoordinator, tempMod));
		
		return tempMod;
	}
	
	/**
	 * Delete the mod's zip file, but do not mark the mod as deleted
	 * @param mod
	 * @param config
	 */
	public void deleteModZip(final Mod mod){
		delete(modMetaHelper.getZipPath(mod));
	}
	
	/**
	 * Fully delete the mod and mark it as deleted.
	 * @param mod
	 * @param config
	 * @param modLoader
	 */
	public void deleteMod(Mod mod) {
		// Try to disable the mod first
		try {
			if (modLoader.isEnabled(mod)){
				disableMod(mod);
			}
		} catch (ModNotDownloadedException e) {
			// Do nothing
		}
		
		addTask(new RemoveModTask(mod, updateCoordinator));
		deleteModZip(mod);
		delete(mod.getCachedImagePath(config));
	}
	
	public void disableMod(Mod mod) throws ModNotDownloadedException{
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
		
		addTask(new SaveModTask.FromMod(updateCoordinator, mod));
	}
	
	public void enableMod(Mod mod) throws ModNotDownloadedException {
		try {
			Path zipPath = modMetaHelper.getZipPath(mod);
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
			
			addTask(new SaveModTask.FromMod(updateCoordinator, mod));
			
		} catch (IOException e) {
			throw new ModNotDownloadedException(mod, e.toString());
		}
	}
}
