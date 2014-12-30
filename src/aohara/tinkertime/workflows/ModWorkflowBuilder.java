package aohara.tinkertime.workflows;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;

import aohara.common.tree.TreeNode;
import aohara.common.workflows.ConflictResolver;
import aohara.common.workflows.WorkflowBuilder;
import aohara.common.workflows.tasks.UnzipTask;
import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.controllers.ModLoader;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.models.FileUpdateListener;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModStructure;
import aohara.tinkertime.workflows.tasks.CacheCrawlerPageTask;
import aohara.tinkertime.workflows.tasks.CheckForUpdateTask;
import aohara.tinkertime.workflows.tasks.CrawlerDownloadTask;
import aohara.tinkertime.workflows.tasks.MarkModEnabledTask;
import aohara.tinkertime.workflows.tasks.MarkModUpdatedTask;
import aohara.tinkertime.workflows.tasks.MoveCrawlerDownloadToDestTask;
import aohara.tinkertime.workflows.tasks.NotfiyUpdateAvailableTask;

public class ModWorkflowBuilder extends WorkflowBuilder {
	
	public static enum ModDownloadType { File, Image };
	
	public ModWorkflowBuilder(String workflowName) {
		super(workflowName);
	}
	
	/**
	 * Notifies the listeners if an update is available for the given file
	 */
	public void checkForUpdates(Mod mod, FileUpdateListener... listeners) throws IOException, UnsupportedHostException {
		checkForUpdates(mod.getPageUrl(), mod.getUpdatedOn(), mod.getNewestFileName(), listeners);
	}
	
	public void checkForUpdates(URL pageUrl, Date updatedOn, String newestFileName, FileUpdateListener... listeners) throws UnsupportedHostException{
		DownloaderContext context = DirectDownloaderContext.fromUrl(pageUrl, null, null);
		addTask(new CacheCrawlerPageTask(context));
		addTask(new CheckForUpdateTask(context, updatedOn, newestFileName));
		addTask(new NotfiyUpdateAvailableTask(context.crawler, listeners));
	}
	
	/**
	 * Downloads the latest version of the mod referenced by the URL.
	 */
	public void downloadMod(URL pageUrl, TinkerConfig config, ModLoader sm) throws IOException, UnsupportedHostException {
		ModDownloaderContext context = ModDownloaderContext.create(pageUrl, config);
		addTask(new CacheCrawlerPageTask(context));
		
		// Download File
		Path tempFile = Files.createTempFile("temp", ".download");
		tempFile.toFile().deleteOnExit();
		addTask(new CrawlerDownloadTask(context.crawler, ModDownloadType.File, tempFile));
		addTask(new MoveCrawlerDownloadToDestTask(context, ModDownloadType.File, tempFile));
		
		// Download Image
		Path tempImage = Files.createTempFile("temp", ".download");
		tempImage.toFile().deleteOnExit();
		addTask(new CrawlerDownloadTask(context.crawler, ModDownloadType.Image, tempImage));
		addTask(new MoveCrawlerDownloadToDestTask(context, ModDownloadType.Image, tempImage));
		
		addTask(MarkModUpdatedTask.createFromDownloaderContext(sm, context));
	}
	
	public void addLocalMod(Path zipPath, TinkerConfig config, ModLoader sm){
		String fileName = zipPath.getFileName().toString();
		String prettyName = fileName;
		if (prettyName.indexOf(".") > 0)
			prettyName = prettyName.substring(0, prettyName.lastIndexOf("."));
		//prettyName = prettyName.replaceAll("_", " ");
		Mod newMod = new Mod(
			fileName, prettyName, fileName, null, null, null,
			Calendar.getInstance().getTime(), null
		);
		
		copy(zipPath, newMod.getCachedZipPath(config));
		addTask(MarkModUpdatedTask.createFromMod(sm, newMod));
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
		if (mod.isEnabled()){
			try {
				disableMod(mod, config, sm);
			} catch (IOException e) {
				// No Action
			}
		}
		
		deleteModZip(mod, config);
		delete(mod.getCachedImagePath(config));
		addTask(MarkModUpdatedTask.notifyDeletion(sm, mod, config));
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
		addTask(new MarkModEnabledTask(mod, sm, false));
	}
	
	public void enableMod(Mod mod, TinkerConfig config, ModLoader sm, ConflictResolver cr) throws IOException{
		if (modHasArchive(mod, config)){
			ModStructure structure = ModStructure.inspectArchive(config, mod);
			for (TreeNode module : structure.getModules()){
				addTask(new UnzipTask(mod.getCachedZipPath(config), config.getGameDataPath(), module, cr));
			}
		} else {
			copy(mod.getCachedZipPath(config), config.getGameDataPath());
		}
		
		addTask(new MarkModEnabledTask(mod, sm, true));
	}
	
	// helpers
	
	private boolean isDependency(TreeNode module, TinkerConfig config, ModLoader sm) throws IOException{
		int numDependencies = 0;
		for (Mod mod : sm.getMods()){
			try {
				if (mod.isEnabled() && modHasArchive(mod, config) && ModStructure.inspectArchive(config, mod).usesModule(module)){
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
