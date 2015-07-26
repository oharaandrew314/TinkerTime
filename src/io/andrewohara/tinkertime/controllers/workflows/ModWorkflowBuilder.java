package io.andrewohara.tinkertime.controllers.workflows;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;

import io.andrewohara.common.workflows.tasks.WorkflowBuilder;
import io.andrewohara.tinkertime.controllers.ModLoader;
import io.andrewohara.tinkertime.controllers.ModManager.ModNotDownloadedException;
import io.andrewohara.tinkertime.controllers.workflows.tasks.AnalyzeModZipTask;
import io.andrewohara.tinkertime.controllers.workflows.tasks.CheckForUpdateTask;
import io.andrewohara.tinkertime.controllers.workflows.tasks.DownloadModImageTask;
import io.andrewohara.tinkertime.controllers.workflows.tasks.DownloadModInBrowserTask;
import io.andrewohara.tinkertime.controllers.workflows.tasks.DownloadModZipTask;
import io.andrewohara.tinkertime.controllers.workflows.tasks.RemoveModTask;
import io.andrewohara.tinkertime.controllers.workflows.tasks.RunCrawlerTask;
import io.andrewohara.tinkertime.controllers.workflows.tasks.SaveModTask;
import io.andrewohara.tinkertime.io.crawlers.Crawler;
import io.andrewohara.tinkertime.io.crawlers.CrawlerFactory;
import io.andrewohara.tinkertime.io.crawlers.CrawlerFactory.UnsupportedHostException;
import io.andrewohara.tinkertime.models.ModFile;
import io.andrewohara.tinkertime.models.mod.Mod;

public class ModWorkflowBuilder extends WorkflowBuilder {

	private final CrawlerFactory crawlerService;
	private final ModLoader modLoader;
	private final Dao<ModFile, Integer> modFilesDao;

	private Crawler<?> cachedCrawler;

	@Inject
	public ModWorkflowBuilder(CrawlerFactory crawlerService, ModLoader modLoader, Dao<ModFile, Integer> modFilesDao, Mod mod) {
		super(mod);
		this.crawlerService = crawlerService;
		this.modLoader = modLoader;
		this.modFilesDao = modFilesDao;
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
		addTask(new CheckForUpdateTask(getCrawler(), getMod(),markIfAvailable));
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
		addTask(new DownloadModZipTask(crawler, getMod()));
		addTask(new DownloadModImageTask(crawler, getMod()));
		addTask(new AnalyzeModZipTask(getMod(), modFilesDao));
		addTask(new SaveModTask(crawler, getMod()));
	}

	public void downloadModInBrowser(Mod mod) throws UnsupportedHostException{
		addTask(new DownloadModInBrowserTask(getCrawler(), getMod()));
	}

	public void addLocalMod(Path zipPath){
		// Add Mod
		copy(zipPath, getMod().getZipPath());
		addTask(new AnalyzeModZipTask(getMod(), modFilesDao));
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

		addTask(new RemoveModTask(getMod()));
		deleteModZip();
	}

	public void disableMod() throws ModNotDownloadedException{
		Set<Path> modDestPaths = getModDestPaths(getMod());

		// Check if any files for this mod are dependencies of other mods.
		// All files which are a dependency will not be deleted
		for (Mod otherMod : modLoader.getMods()){
			if (otherMod != null && !otherMod.equals(getMod()) && otherMod.isEnabled()){
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
