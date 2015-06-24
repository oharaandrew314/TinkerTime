package aohara.tinkertime.workflows;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import aohara.common.workflows.tasks.WorkflowBuilder;
import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.controllers.ModExceptions.ModNotDownloadedException;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.CrawlerFactory;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.resources.ModMetaLoader;
import aohara.tinkertime.workflows.DownloadModAssetTask.ModDownloadType;

public class ModWorkflowBuilder extends WorkflowBuilder {
	
	private final CrawlerFactory crawlerService;
	private Crawler<?> cachedCrawler;
	
	public ModWorkflowBuilder(Mod context, CrawlerFactory crawlerService) {
		super(context);
		this.crawlerService = crawlerService;
		
		if (context == null){
			throw new IllegalArgumentException("Context Cannot be null");
		}
	}
	
	private Mod getContextMod(){
		return (Mod) context;
	}
	
	private Crawler<?> getCrawler() throws UnsupportedHostException{
		return (cachedCrawler != null) ? cachedCrawler : (cachedCrawler = crawlerService.getCrawler(getContextMod().pageUrl));
	}
	
	/**
	 * Notifies the listeners if an update is available for the given file
	 * @throws UnsupportedHostException 
	 */
	public void checkForUpdates(ModMetaLoader modLoader, boolean markIfAvailable) throws UnsupportedHostException {
		addTask(new CheckForUpdateTask(getCrawler(), getContextMod().getVersion(), getContextMod().updatedOn));
		if (markIfAvailable){
			addTask(new MarkModUpdatedTask(modLoader, getContextMod()));
		}
	}
	
	public void downloadNewMod(TinkerConfig config, ModMetaLoader modLoader) throws UnsupportedHostException {		
		addTask(new SaveModTask.FromMod(modLoader, getContextMod()));  // Create Placeholder Mod
		downloadMod(config, modLoader);  // DownloadMod
	}
	
	/**
	 * Downloads the latest version of the mod referenced by the URL.
	 * @throws UnsupportedHostException 
	 */
	public void updateMod(TinkerConfig config, ModMetaLoader modLoader, boolean forceUpdate) throws UnsupportedHostException {
		// Cleanup operations prior to update
		if (modLoader.isDownloaded(getContextMod())){
			if (!forceUpdate){
				checkForUpdates(modLoader, true);
			}
			
			// Disable Mod if it is enabled
			try {
				if (modLoader.isEnabled(getContextMod())){
					disableMod(getContextMod(), modLoader);
				}
			} catch (ModNotDownloadedException e) {
				// Do Nothing
			}
			
			addTask(new RunCrawlerTask(getCrawler()));  // Get user to select asset before deleting
			deleteModZip(getContextMod(), modLoader);
		}
		
		downloadMod(config, modLoader);
	}
	
	private void downloadMod(TinkerConfig config, ModMetaLoader modLoader) throws UnsupportedHostException{
		addTask(new RunCrawlerTask(getCrawler()));  // prefetch metadata
		addTask(new DownloadModAssetTask(getCrawler(), config, modLoader, ModDownloadType.File));
		addTask(new DownloadModAssetTask(getCrawler(), config, modLoader, ModDownloadType.Image));
		addTask(new SaveModTask.FromCrawler(modLoader, getCrawler()));
	}
	
	public void downloadModInBrowser() throws UnsupportedHostException{
		addTask(new DownloadModInBrowserTask(getCrawler(), getContextMod().getVersion()));
	}
	
	public void addLocalMod(Path zipPath, ModMetaLoader modLoader){
		// Create Placeholder Mod
		addTask(new SaveModTask.FromMod(modLoader, getContextMod()));
		
		// Add Mod
		copy(zipPath, modLoader.getZipPath(getContextMod()));
		addTask(new SaveModTask.FromMod(modLoader, getContextMod()));
	}
	
	/**
	 * Delete the mod's zip file, but do not mark the mod as deleted
	 * @param mod
	 * @param config
	 */
	public void deleteModZip(final Mod mod, final ModMetaLoader modLoader){
		delete(modLoader.getZipPath(mod));
	}
	
	/**
	 * Fully delete the mod and mark it as deleted.
	 * @param mod
	 * @param config
	 * @param modLoader
	 */
	public void deleteMod(Mod mod, TinkerConfig config, ModMetaLoader modLoader) {
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
	
	public void disableMod(Mod mod, ModMetaLoader modLoader) throws ModNotDownloadedException{
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
	
	public void enableMod(Mod mod, ModMetaLoader modLoader, TinkerConfig config) throws ModNotDownloadedException {
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
