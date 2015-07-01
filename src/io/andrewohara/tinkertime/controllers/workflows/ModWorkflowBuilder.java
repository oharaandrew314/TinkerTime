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
import io.andrewohara.tinkertime.db.ModLoader;
import io.andrewohara.tinkertime.io.crawlers.Crawler;
import io.andrewohara.tinkertime.io.crawlers.CrawlerFactory;
import io.andrewohara.tinkertime.io.crawlers.CrawlerFactory.UnsupportedHostException;
import io.andrewohara.tinkertime.models.ModFile;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.inject.Inject;

public class ModWorkflowBuilder extends WorkflowBuilder {

	private final CrawlerFactory crawlerService;
	private final ModUpdateCoordinator updateCoordinator;
	private final ModLoader modLoader;

	private Crawler<?> cachedCrawler;

	@Inject
	public ModWorkflowBuilder(CrawlerFactory crawlerService, ModUpdateCoordinator updateCoordinator, ModLoader modLoader, Mod mod) {
		super(mod);
		this.crawlerService = crawlerService;
		this.updateCoordinator = updateCoordinator;
		this.modLoader = modLoader;
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
		addTask(new CheckForUpdateTask(getCrawler()));
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
		Set<Path> modDestPaths = getModDestPaths(getMod());

		// Check if any files for this mod are dependencies of other mods.
		// All files which are a dependency will not be deleted
		for (Mod otherMod : modLoader.getMods()){
			if (!otherMod.equals(getMod()) && otherMod.isEnabled()){
				modDestPaths.removeAll(getModDestPaths(otherMod));
			}
		}

		delete(modDestPaths);
		cleanupDir(getMod().getInstallation().getGameDataPath());
	}

	private Set<Path> getModDestPaths(Mod mod){
		Set<Path> fileDestPaths = new LinkedHashSet<>();
		for (ModFile modFile : mod.getModFiles()){
			fileDestPaths.add(modFile.getDestPath());
		}
		return fileDestPaths;
	}

	public void enableMod() throws ModNotDownloadedException {
		if (!getMod().isDownloaded()){
			throw new ModNotDownloadedException(getMod(), "mod has no zip path");
		}

		Path zipPath = getMod().getZipPath();
		try (ZipFile zipFile = new ZipFile(zipPath.toFile())){

			for (ZipEntry entry : Collections.list(zipFile.entries())){
				System.out.println(entry.getName());
			}

			Map<Path, ZipEntry> zipData = new LinkedHashMap<>();
			for (ModFile modFile : getMod().getModFiles()){
				zipData.put(modFile.getDestPath(), modFile.getEntry(zipFile));
			}
			unzip(zipPath, zipData);
		} catch (IOException e) {
			throw new ModNotDownloadedException(getMod(), "Error opening zip");
		}
	}
}
