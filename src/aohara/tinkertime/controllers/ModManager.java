package aohara.tinkertime.controllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import aohara.common.Listenable;
import aohara.common.workflows.tasks.TaskCallback;
import aohara.common.workflows.tasks.WorkflowBuilder;
import aohara.tinkertime.ConfigController;
import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.TinkerTime;
import aohara.tinkertime.controllers.ModExceptions.CannotDeleteModException;
import aohara.tinkertime.controllers.ModExceptions.ModNotDownloadedException;
import aohara.tinkertime.controllers.ModExceptions.ModUpdateFailedError;
import aohara.tinkertime.controllers.ModExceptions.NoModSelectedException;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.models.DefaultMods;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.resources.ModMetaLoader;
import aohara.tinkertime.workflows.ModWorkflowBuilder;
import aohara.tinkertime.workflows.ModWorkflowBuilderFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Controller for initiating Asynchronous Tasks for Mod Processing.
 * 
 * All Mod-Related Actions are to be initiated through this Controller.
 * All Asynchronous tasks initiated are executed by the executors of this class,
 * and the tasks are represented by {@link aohara.common.workflows.Workflow} classes.
 * 
 * @author Andrew O'Hara
 */
@Singleton
public class ModManager extends Listenable<TaskCallback> {
	
	private final TinkerConfig config;
	private final ThreadPoolExecutor downloadExecutor;
	private final Executor enablerExecutor;
	private final ModMetaLoader modLoader;
	private final ModWorkflowBuilderFactory workflowBuilderFactory;
	private final ConfigController configController;

	private Mod selectedMod;

	@Inject
	ModManager(ModMetaLoader loader, TinkerConfig config, ThreadPoolExecutor downloadExecutor, Executor enablerExecutor, ModWorkflowBuilderFactory workflowBuilderFactory, ConfigController configController){
		this.modLoader = loader;
		this.config = config;
		this.downloadExecutor = downloadExecutor;
		this.enablerExecutor = enablerExecutor;
		this.workflowBuilderFactory = workflowBuilderFactory;
		this.configController = configController;
	}
	
	// -- Interface --------------------------------------------------------

	public Mod getSelectedMod() throws NoModSelectedException {
		if (selectedMod == null){
			throw new NoModSelectedException();
		}
		return selectedMod;
	}
	
	public void selectMod(Mod mod){
		this.selectedMod = mod;
	}
	
	public void updateMod(Mod mod, boolean forceUpdate) throws ModUpdateFailedError, ModNotDownloadedException {
		if (!mod.isUpdateable()){
			throw new ModUpdateFailedError(mod, "Mod is a local zip only, and cannot be updated.");
		}
		try {
			ModWorkflowBuilder builder = workflowBuilderFactory.createBuilder();
			builder.updateMod(mod, forceUpdate);
			submitDownloadWorkflow(builder, mod);
		} catch (UnsupportedHostException e) {
			throw new ModUpdateFailedError(e);
		}
	}
	
	public void downloadMod(URL url) throws MalformedURLException, UnsupportedHostException {
		ModWorkflowBuilder builder = workflowBuilderFactory.createBuilder();
		Mod tempMod = builder.downloadNewMod(url);
		submitDownloadWorkflow(builder, tempMod);
	}
	
	public void addModZip(Path zipPath){
		ModWorkflowBuilder builder = workflowBuilderFactory.createBuilder();
		Mod tempMod = builder.addLocalMod(zipPath);
		submitDownloadWorkflow(builder, tempMod);
	}
	
	public void updateMods() throws ModUpdateFailedError, ModNotDownloadedException{
		for (Mod mod : modLoader.getMods()){
			if (mod.isUpdateable()){
				updateMod(mod, false);
			}
		}
	}
	
	public void toggleMod(final Mod mod) {
		ModWorkflowBuilder builder = workflowBuilderFactory.createBuilder();
		try {
			if (modLoader.isEnabled(mod)){
				builder.disableMod(mod);
			} else {
				builder.enableMod(mod);
			}			
			submitEnablerWorkflow(builder, mod);
		} catch (ModNotDownloadedException e){
			// Ignore user input if mod not downloaded
		}
	}	
	
	public void deleteMod(final Mod mod) throws CannotDeleteModException {
		if (DefaultMods.isBuiltIn(mod)){
			throw new CannotDeleteModException(mod, "Built-in");
		}
		ModWorkflowBuilder builder = workflowBuilderFactory.createBuilder();
		builder.deleteMod(mod);
		submitEnablerWorkflow(builder, mod);
	}
	
	public void checkForModUpdates() throws Exception{
		Exception e = null;
		
		for (final Mod mod : modLoader.getMods()){
			try {
				if (mod.isUpdateable()){
					ModWorkflowBuilder builder = workflowBuilderFactory.createBuilder();
					builder.checkForUpdates(mod, true);
					submitDownloadWorkflow(builder, mod);
				}
			} catch (UnsupportedHostException ex) {
				ex.printStackTrace();
				e = ex;
			}
		}
		
		if (e != null){
			throw e;
		}
	}
	
	public void exportEnabledMods(Path path){
		modLoader.exportEnabledMods(path);
	}
	
	public void importMods(Path path){
		modLoader.importMods(path);
	}
	
	public void tryUpdateModManager() throws UnsupportedHostException, MalformedURLException {
		ModWorkflowBuilder builder = workflowBuilderFactory.createBuilder();
		Mod tempMod = Mod.newTempMod(new URL(TinkerTime.DOWNLOAD_URL), TinkerTime.VERSION);
		builder.checkForUpdates(tempMod, false);
		builder.downloadModInBrowser(tempMod);
		submitDownloadWorkflow(builder, tempMod);
	}
	
	public void reloadMods(){
		configController.reloadMods();
	}
	
	// -- Helpers -----------------------------------------------------------
	
	private void submitDownloadWorkflow(WorkflowBuilder builder, Mod context){
		for(TaskCallback callback : getListeners()){
			builder.addListener(callback);
		}
		
		// Reset thread pool size if size in options has changed
		int numDownloadThreads = config.numConcurrentDownloads();
		if (downloadExecutor.getMaximumPoolSize() != numDownloadThreads){
			downloadExecutor.setCorePoolSize(numDownloadThreads);
			downloadExecutor.setMaximumPoolSize(numDownloadThreads);
		}
		
		builder.execute(downloadExecutor, context);
	}
	
	private void submitEnablerWorkflow(WorkflowBuilder builder, Mod context){
		for(TaskCallback callback : getListeners()){
			builder.addListener(callback);
		}
		builder.execute(enablerExecutor, context);
	}
}
