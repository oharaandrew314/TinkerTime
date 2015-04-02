package aohara.tinkertime.workflows;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import aohara.common.VersionParser;
import aohara.common.workflows.tasks.TaskCallback;
import aohara.common.workflows.tasks.WorkflowBuilder;
import aohara.common.workflows.tasks.WorkflowTask.TaskEvent;
import aohara.tinkertime.ModManager.ModNotDownloadedException;
import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.resources.ModLoader;
import aohara.tinkertime.workflows.DownloadModAssetTask.ModDownloadType;

import com.github.zafarkhaja.semver.Version;

public class ModWorkflowBuilder extends WorkflowBuilder {
	
	public ModWorkflowBuilder(String workflowName) {
		super(workflowName);
	}
	
	/**
	 * Notifies the listeners if an update is available for the given file
	 */
	public void checkForUpdates(Mod mod, Crawler<?> crawler) throws IOException, UnsupportedHostException {
		checkForUpdates(crawler, mod.getVersion(), mod.updatedOn);
	}
	
	public void checkForUpdates(Crawler<?> crawler, Version currentVersion, Date lastUpdatedOn) throws UnsupportedHostException{
		addTask(new CacheCrawlerPageTask(crawler));
		addTask(new CheckForUpdateTask(crawler, currentVersion, lastUpdatedOn));
	}
	
	/**
	 * Downloads the latest version of the mod referenced by the URL.
	 */
	public void downloadMod(Crawler<?> crawler, TinkerConfig config, ModLoader modLoader)
			throws IOException, UnsupportedHostException
	{
		addTask(new CacheCrawlerPageTask(crawler));
		
		addTask(new DownloadModAssetTask(crawler, config, modLoader, ModDownloadType.File));
		addTask(new DownloadModAssetTask(crawler, config, modLoader, ModDownloadType.Image));
	}
	
	public Mod addLocalMod(Path zipPath, ModLoader modLoader){
		String fileName = zipPath.getFileName().toString();
		String prettyName = fileName;
		if (prettyName.indexOf(".") > 0) {
			prettyName = prettyName.substring(0, prettyName.lastIndexOf("."));
		}
		Mod newMod = new Mod(
			fileName, prettyName, fileName, null, null,
			Calendar.getInstance().getTime(), null,
			Version.valueOf(VersionParser.parseVersionString(prettyName))
		);
		
		copy(zipPath, modLoader.getZipPath(newMod));
		return newMod;
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
			e.printStackTrace();  // Do nothing
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
		} catch (IOException e) {
			throw new ModNotDownloadedException(mod, e.toString());
		}
	}
	
	// helpers
	
	public void refreshModAfterWorkflowComplete(final Mod mod, final ModLoader loader){
		addListener(new TaskCallback.WorkflowCompleteCallback() {
			
			@Override
			protected void processTaskEvent(TaskEvent event) {
				loader.modUpdated(mod);
			}
		});
	}
}
