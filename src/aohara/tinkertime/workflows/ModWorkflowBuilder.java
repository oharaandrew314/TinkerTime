package aohara.tinkertime.workflows;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Calendar;

import aohara.common.tree.TreeNode;
import aohara.common.workflows.ConflictResolver;
import aohara.common.workflows.tasks.TaskCallback;
import aohara.common.workflows.tasks.WorkflowBuilder;
import aohara.common.workflows.tasks.WorkflowTask.TaskEvent;
import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.controllers.ModLoader;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.crawlers.VersionInfo;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModStructure;
import aohara.tinkertime.workflows.DownloadModAssetTask.ModDownloadType;

public class ModWorkflowBuilder extends WorkflowBuilder {
	
	public ModWorkflowBuilder(String workflowName) {
		super(workflowName);
	}
	
	/**
	 * Notifies the listeners if an update is available for the given file
	 */
	public void checkForUpdates(Mod mod, Crawler<?> crawler) throws IOException, UnsupportedHostException {
		checkForUpdates(crawler, new VersionInfo(null, mod.getUpdatedOn(), mod.getNewestFileName()));
	}
	
	public void checkForUpdates(Crawler<?> crawler, VersionInfo currentVersion) throws UnsupportedHostException{
		addTask(new CacheCrawlerPageTask(crawler));
		addTask(new CheckForUpdateTask(crawler, currentVersion));
	}
	
	/**
	 * Downloads the latest version of the mod referenced by the URL.
	 */
	public void downloadMod(Crawler<?> crawler, TinkerConfig config, ModLoader sm) throws IOException, UnsupportedHostException {
		addTask(new CacheCrawlerPageTask(crawler));
		
		addTask(new DownloadModAssetTask(crawler, config, ModDownloadType.File));
		addTask(new DownloadModAssetTask(crawler, config, ModDownloadType.Image));
	}
	
	public Mod addLocalMod(Path zipPath, TinkerConfig config, ModLoader sm){
		String fileName = zipPath.getFileName().toString();
		String prettyName = fileName;
		if (prettyName.indexOf(".") > 0) {
			prettyName = prettyName.substring(0, prettyName.lastIndexOf("."));
		}
		Mod newMod = new Mod(
			fileName, prettyName, fileName, null, null,
			Calendar.getInstance().getTime(), null
		);
		
		copy(zipPath, newMod.getCachedZipPath(config));
		return newMod;
	}
	
	/**
	 * Delete the mod's zip file, but do not mark the mod as deleted
	 * @param mod
	 * @param config
	 */
	public void deleteModZip(final Mod mod, final TinkerConfig config){
		delete(mod.getCachedZipPath(config));
	}
	
	/**
	 * Fully delete the mod and mark it as deleted.
	 * @param mod
	 * @param config
	 * @param sm
	 */
	public void deleteMod(Mod mod, TinkerConfig config, ModLoader sm) {
		if (mod.isEnabled(config)){
			try {
				disableMod(mod, config, sm);
			} catch (IOException e) {
				// No Action
			}
		}
		
		deleteModZip(mod, config);
		delete(mod.getCachedImagePath(config));
	}
	
	public void disableMod(Mod mod, TinkerConfig config, ModLoader sm) throws IOException{
		if (modHasArchive(mod, config)){			
			for (TreeNode module : ModStructure.inspectArchive(config, mod).getModules()){
				
				if (!isDependency(module, config, sm)){
					delete(config.getGameDataPath().resolve(module.getName()));
				}
			}
		} else {
			delete(config.getGameDataPath().resolve(mod.getNewestFileName()));
		}
	}
	
	public void enableMod(Mod mod, TinkerConfig config, ModLoader sm, ConflictResolver cr) throws IOException{
		if (modHasArchive(mod, config)){
			ModStructure structure = ModStructure.inspectArchive(config, mod);
			for (TreeNode module : structure.getModules()){
				unzip(mod.getCachedZipPath(config), config.getGameDataPath(), module, cr);
			}
		} else {
			copy(mod.getCachedZipPath(config), config.getGameDataPath());
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
	
	private boolean isDependency(TreeNode module, TinkerConfig config, ModLoader sm) throws IOException{
		int numDependencies = 0;
		for (Mod mod : sm.getMods()){
			try {
				if (mod.isEnabled(config) && modHasArchive(mod, config) && ModStructure.inspectArchive(config, mod).usesModule(module)){
					numDependencies++;
				}
			} catch (FileNotFoundException ex){}
		}
		return numDependencies > 1;
	}
	
	private boolean modHasArchive(Mod mod, TinkerConfig config){
		return mod.getNewestFileName().toLowerCase().endsWith(".zip") && mod.isDownloaded(config);
	}
}
