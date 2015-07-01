package io.andrewohara.tinkertime.controllers.workflows;

import io.andrewohara.common.workflows.tasks.WorkflowBuilder;
import io.andrewohara.tinkertime.controllers.ModExceptions.ModNotDownloadedException;
import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateCoordinator;
import io.andrewohara.tinkertime.controllers.workflows.tasks.AnalyzeModZipTask;
import io.andrewohara.tinkertime.controllers.workflows.tasks.CheckForUpdateTask;
import io.andrewohara.tinkertime.controllers.workflows.tasks.DownloadModAssetTask;
import io.andrewohara.tinkertime.controllers.workflows.tasks.DownloadModAssetTask.ModDownloadType;
import io.andrewohara.tinkertime.controllers.workflows.tasks.DownloadModInBrowserTask;
import io.andrewohara.tinkertime.controllers.workflows.tasks.MarkModUpdatedTask;
import io.andrewohara.tinkertime.controllers.workflows.tasks.RemoveModTask;
import io.andrewohara.tinkertime.controllers.workflows.tasks.RunCrawlerTask;
import io.andrewohara.tinkertime.controllers.workflows.tasks.SaveModTask;
import io.andrewohara.tinkertime.io.crawlers.Crawler;
import io.andrewohara.tinkertime.io.crawlers.CrawlerFactory;
import io.andrewohara.tinkertime.io.crawlers.CrawlerFactory.UnsupportedHostException;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.net.MalformedURLException;
import java.nio.file.Path;

import com.google.inject.Inject;

public class ModWorkflowBuilder extends WorkflowBuilder {

	private final CrawlerFactory crawlerService;
	private final ModUpdateCoordinator updateCoordinator;

	private Crawler<?> cachedCrawler;

	@Inject
	public ModWorkflowBuilder(CrawlerFactory crawlerService, ModUpdateCoordinator updateCoordinator, Mod mod) {
		super(mod);
		this.crawlerService = crawlerService;
		this.updateCoordinator = updateCoordinator;
	}

	private Mod getMod(){
		return (Mod) context;
	}

	private Crawler<?> getCrawler() throws UnsupportedHostException{
		return (cachedCrawler != null) ? cachedCrawler : (cachedCrawler = crawlerService.getCrawler(getMod()));
	}

	/**
	 * Notifies the listeners if an update is available for the given file
	 * @throws UnsupportedHostException
	 */
	public void checkForUpdates(boolean markIfAvailable) throws UnsupportedHostException {
		addTask(new CheckForUpdateTask(getCrawler(), getMod().getModVersion(), getMod().getUpdatedOn()));  // TODO Just pass in mod
		if (markIfAvailable){
			addTask(new MarkModUpdatedTask(updateCoordinator, getMod()));
		}
	}

	public void downloadNewMod() throws UnsupportedHostException, MalformedURLException {
		addTask(new SaveModTask.FromMod(updateCoordinator, getMod()));
		downloadMod();
	}

	/**
	 * Downloads the latest version of the mod referenced by the URL.
	 * @throws UnsupportedHostException
	 */
	public void updateMod(boolean forceUpdate) throws UnsupportedHostException {
		// Cleanup operations prior to update
		if (getMod().isDownloaded()){
			if (!forceUpdate){
				checkForUpdates(true);
			}

			// Disable Mod if it is enabled
			try {
				if (getMod().isEnabled()){
					disableMod();
				}
			} catch (ModNotDownloadedException e) {
				// Do Nothing
			}

			addTask(new RunCrawlerTask(getCrawler()));  // Get user to select asset before deleting
			deleteModZip();
		}

		downloadMod();
	}

	private void downloadMod() throws UnsupportedHostException{
		Crawler<?> crawler = getCrawler();
		addTask(new RunCrawlerTask(crawler));  // prefetch metadata
		addTask(new DownloadModAssetTask(crawler, ModDownloadType.File));
		addTask(new DownloadModAssetTask(crawler, ModDownloadType.Image));
		addTask(new AnalyzeModZipTask(getMod(), getMod().getZipPath(), updateCoordinator));
		addTask(new SaveModTask.FromCrawler(updateCoordinator, crawler));
	}

	public void downloadModInBrowser(Mod mod) throws UnsupportedHostException{
		addTask(new DownloadModInBrowserTask(getCrawler(), mod.getModVersion()));
	}

	public void addLocalMod(Path zipPath){
		// Create Placeholder Mod
		addTask(new SaveModTask.FromMod(updateCoordinator, getMod()));

		// Add Mod
		copy(zipPath, getMod().getZipPath());
		addTask(new SaveModTask.FromMod(updateCoordinator, getMod()));
	}

	/**
	 * Delete the mod's zip file, but do not mark the mod as deleted
	 * @param mod
	 * @param config
	 */
	public void deleteModZip(){
		delete(getMod().getZipPath());
	}

	/**
	 * Fully delete the mod and mark it as deleted.
	 * @param mod
	 * @param config
	 * @param modLoader
	 */
	public void deleteMod() {
		// Try to disable the mod first
		try {
			if (getMod().isEnabled()){
				disableMod();
			}
		} catch (ModNotDownloadedException e) {
			// Do nothing
		}

		addTask(new RemoveModTask(getMod(), updateCoordinator));
		deleteModZip();
		delete(getMod().getImagePath());
	}

	public void disableMod() throws ModNotDownloadedException{
		//TODO Reimplement disableMod
		/*
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
		 */
	}

	public void enableMod(Mod mod) throws ModNotDownloadedException {
		//TODO Reimplement enableMod
		/*
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
		 */
	}
}
