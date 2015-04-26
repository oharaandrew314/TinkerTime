package aohara.tinkertime;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.JOptionPane;

import com.github.zafarkhaja.semver.Version;

import aohara.common.Listenable;
import aohara.common.workflows.tasks.BrowserGoToTask;
import aohara.common.workflows.tasks.TaskCallback;
import aohara.common.workflows.tasks.WorkflowBuilder;
import aohara.common.workflows.tasks.WorkflowTask.TaskEvent;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.CrawlerFactory;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.models.DefaultMods;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.resources.ModLoader;
import aohara.tinkertime.workflows.CheckForUpdateTask;
import aohara.tinkertime.workflows.CheckForUpdateTask.OnUpdateAvailable;
import aohara.tinkertime.workflows.ModWorkflowBuilder;

/**
 * Controller for initiating Asynchronous Tasks for Mod Processing.
 * 
 * All Mod-Related Actions are to be initiated through this Controller.
 * All Asynchronous tasks initiated are executed by the executors of this class,
 * and the tasks are represented by {@link aohara.common.workflows.Workflow} classes.
 * 
 * @author Andrew O'Hara
 */
public class ModManager extends Listenable<TaskCallback> {
	
	public final TinkerConfig config;
	
	private final CrawlerFactory crawlerFactory;
	private final ThreadPoolExecutor downloadExecutor;
	private final Executor enablerExecutor;
	private final ModLoader modLoader;
	
	private Mod selectedMod;

	public ModManager(
			ModLoader loader, TinkerConfig config,
			ThreadPoolExecutor downloadExecutor,
			Executor enablerExecutor, CrawlerFactory crawlerFactory
	){
		this.modLoader = loader;
		this.config = config;
		this.downloadExecutor = downloadExecutor;
		this.enablerExecutor = enablerExecutor;
		this.crawlerFactory = crawlerFactory;
	}
	
	// -- Interface --------------------------------------------------------

	public Mod getSelectedMod() throws NoModSelectedException {
		if (selectedMod == null){
			throw new NoModSelectedException();
		}
		return selectedMod;
	}
	
	void selectMod(Mod mod){
		this.selectedMod = mod;
	}
	
	/**
	 * 
	 * @param mod mod to be updated 
	 * @param forceUpdate update even if newer update is not available
	 * @throws ModUpdateFailedError
	 * @throws ModNotDownloadedException 
	 */
	public void updateMod(Mod mod, boolean forceUpdate) throws ModUpdateFailedError, ModNotDownloadedException {
		if (!mod.isUpdateable()){
			throw new ModUpdateFailedError(mod, "Mod is a local zip only, and cannot be updated.");
		}
		
		ModWorkflowBuilder builder = new ModWorkflowBuilder(mod);
		try {
			// Cleanup operations prior to update
			if (modLoader.isDownloaded(mod)){
				if (!forceUpdate){
					builder.checkForUpdates(getCrawler(mod), mod.getVersion(), mod.updatedOn, null);
				}
				
				if (modLoader.isEnabled(mod)){
					builder.disableMod(mod, modLoader);
				}
				
				builder.deleteModZip(mod, modLoader);
			}
			builder.updateMod(getCrawler(mod), config, modLoader);
			submitDownloadWorkflow(builder);
		} catch (UnsupportedHostException e) {
			throw new ModUpdateFailedError(e);
		}
	}
	
	public void downloadMod(URL url) throws UnsupportedHostException {
		ModWorkflowBuilder builder = new ModWorkflowBuilder(null);
		builder.downloadNewMod(getCrawler(url), config, modLoader);
		submitDownloadWorkflow(builder);
	}
	
	public void addModZip(Path zipPath){
		ModWorkflowBuilder builder = new ModWorkflowBuilder(null);
		builder.addLocalMod(zipPath, modLoader);
		submitDownloadWorkflow(builder);
	}
	
	public void updateMods() throws ModUpdateFailedError, ModNotDownloadedException{
		for (Mod mod : modLoader.getMods()){
			updateMod(mod, false);
		}
	}
	
	public void toggleMod(final Mod mod) {
		ModWorkflowBuilder builder = new ModWorkflowBuilder(mod);
		try {
			if (modLoader.isEnabled(mod)){
				builder.disableMod(mod, modLoader);
			} else {
				builder.enableMod(mod, modLoader, config);
			}			
			submitEnablerWorkflow(builder);
		} catch (ModNotDownloadedException e){
			// Ignore user input if mod not downloaded
		}
	}	
	
	public void deleteMod(final Mod mod) throws CannotDeleteModException {
		if (DefaultMods.isBuiltIn(mod)){
			throw new CannotDeleteModException(mod, "Built-in");
		}
		
		ModWorkflowBuilder builder = new ModWorkflowBuilder(mod);
		builder.deleteMod(mod, config, modLoader);
		builder.addListener(new TaskCallback.WorkflowCompleteCallback() {
			
			@Override
			protected void processTaskEvent(TaskEvent event) {
				modLoader.modDeleted(mod);
			}
		});
		
		submitEnablerWorkflow(builder);
	}
	
	public void checkForModUpdates() throws Exception{
		Exception e = null;
		
		for (final Mod mod : modLoader.getMods()){
			try {
				if (mod.isUpdateable()){
					ModWorkflowBuilder builder = new ModWorkflowBuilder(mod);
					OnUpdateAvailable onUpdateAvailable = new CheckForUpdateTask.OnUpdateAvailable() {
						@Override
						public void onUpdateAvailable(Version newVersion, URL downloadLink) {
							mod.updateAvailable = true;
							modLoader.modUpdated(mod);
						}
					};
					
					builder.checkForUpdates(getCrawler(mod), mod.getVersion(), mod.updatedOn, onUpdateAvailable);
					submitDownloadWorkflow(builder);
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
		modLoader.importMods(path, this);
	}
	
	/**
	 * Launches an update task to check for the latest update to the Mod Manager.
	 * 
	 * If an update is available from Github, then the user is given a choice to update.
	 * @throws UnsupportedHostException 
	 */
	public void tryUpdateModManager() throws UnsupportedHostException {
		ModWorkflowBuilder builder = new ModWorkflowBuilder(null);
		try {
			OnUpdateAvailable onUpdateAvailable = new CheckForUpdateTask.OnUpdateAvailable() {
				
				@Override
				public void onUpdateAvailable(Version newVersion, URL downloadLink) {
					if (JOptionPane.showConfirmDialog(
						null,
						String.format(
							"%s v%s is available.%n" +
							"Would you like to download it?%n" +
							"%n" + 
							"You currently have v%s",
							TinkerTime.NAME, newVersion, TinkerTime.VERSION
						),
						"Update Tinker Time",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE
					) == JOptionPane.YES_OPTION){
						BrowserGoToTask.callNow(downloadLink);
					}
				}
			};
			
			builder.checkForUpdates(
				getCrawler(new URL(CrawlerFactory.APP_UPDATE_URL)),
				TinkerTime.VERSION, null, onUpdateAvailable
			);
			submitDownloadWorkflow(builder);
		} catch (MalformedURLException e) { /* Ignore */ }
	}
	
	public void openConfigWindow(){
		config.updateConfig();
		reloadMods();
	}
	
	private void reloadMods(){
		modLoader.init(this);  // Reload mods (top update views)
	}
	
	// -- Helpers -----------------------------------------------------------
	
	private Crawler<?> getCrawler(URL url) throws UnsupportedHostException{
		return crawlerFactory.getCrawler(url);
	}
	
	private Crawler<?> getCrawler(Mod mod) throws UnsupportedHostException{
		return getCrawler(mod.pageUrl);
	}
	
	private void submitDownloadWorkflow(WorkflowBuilder builder){
		for(TaskCallback callback : getListeners()){
			builder.addListener(callback);
		}
		
		// Reset thread pool size if size in options has changed
		int numDownloadThreads = config.numConcurrentDownloads();
		if (downloadExecutor.getMaximumPoolSize() != numDownloadThreads){
			downloadExecutor.setCorePoolSize(numDownloadThreads);
			downloadExecutor.setMaximumPoolSize(numDownloadThreads);
		}
		
		builder.execute(downloadExecutor);
	}
	
	private void submitEnablerWorkflow(WorkflowBuilder builder){
		for(TaskCallback callback : getListeners()){
			builder.addListener(callback);
		}
		builder.execute(enablerExecutor);
	}
	
	// -- Exceptions/Errors --------------------------------------------------
	
	@SuppressWarnings("serial")
	public static class ModNotDownloadedException extends Exception {
		public ModNotDownloadedException(Mod mod, String message){
			super("Error for " + mod + ": " + message);
		}
	}
	@SuppressWarnings("serial")
	public static class CannotDisableModError extends Error {}
	@SuppressWarnings("serial")
	public static class ModUpdateFailedError extends Error {
		private ModUpdateFailedError(Exception e){
			super(e);
		}
		private ModUpdateFailedError(Mod mod, String message){
			super("Error for " + mod + ": " + message);
		}
	}
	@SuppressWarnings("serial")
	public static class NoModSelectedException extends Exception {}
	@SuppressWarnings("serial")
	public static class CannotDeleteModException extends Exception {
		private CannotDeleteModException(Mod mod, String reason){
			super(String.format("Cannot delete %s: %s", mod, reason));
		}
		
	}
}
