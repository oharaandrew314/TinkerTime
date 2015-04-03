package aohara.tinkertime;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.JOptionPane;

import org.apache.commons.io.FilenameUtils;

import aohara.common.workflows.ProgressPanel;
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
public class ModManager {
	
	public final TinkerConfig config;
	
	private final CrawlerFactory crawlerFactory;
	private final ThreadPoolExecutor downloadExecutor;
	private final Executor enablerExecutor;
	private final ModLoader loader;
	private final ProgressPanel progressPanel;
	
	private Mod selectedMod;

	public ModManager(
			ModLoader loader, TinkerConfig config, ProgressPanel progressPanel,
			ThreadPoolExecutor downloadExecutor,
			Executor enablerExecutor, CrawlerFactory crawlerFactory
	){
		this.loader = loader;
		this.config = config;
		this.progressPanel = progressPanel;
		this.downloadExecutor = downloadExecutor;
		this.enablerExecutor = enablerExecutor;
		this.crawlerFactory = crawlerFactory;
	}
	
	// -- Accessors --------------------------------------------------------

	public Mod getSelectedMod() throws NoModSelectedException {
		if (selectedMod == null){
			throw new NoModSelectedException();
		}
		return selectedMod;
	}
	
	// -- Modifiers ---------------------------------
	
	void selectMod(Mod mod){
		this.selectedMod = mod;
	}
	
	private void submitDownloadWorkflow(WorkflowBuilder builder){
		builder.addListener(progressPanel);
		
		// Reset thread pool size if size in options has changed
		int numDownloadThreads = config.numConcurrentDownloads();
		if (downloadExecutor.getMaximumPoolSize() != numDownloadThreads){
			downloadExecutor.setCorePoolSize(numDownloadThreads);
			downloadExecutor.setMaximumPoolSize(numDownloadThreads);
		}
		
		builder.execute(downloadExecutor);
	}
	
	private void submitEnablerWorkflow(WorkflowBuilder builder){
		builder.addListener(progressPanel);
		builder.execute(enablerExecutor);
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
		
		ModWorkflowBuilder builder = new ModWorkflowBuilder("Updating " + mod.name);
		try {
			// Cleanup operations prior to update
			if (loader.isDownloaded(mod)){
				if (!forceUpdate){
					builder.checkForUpdates(mod, getCrawler(mod));
				}
				
				if (loader.isEnabled(mod)){
					builder.disableMod(mod, loader);
				}
				
				builder.deleteModZip(mod, loader);
			}
			builder.downloadMod(getCrawler(mod), config, loader);
			builder.addListener(new TaskCallback.WorkflowCompleteCallback() {
				
				@Override
				protected void processTaskEvent(TaskEvent event) {
					Mod mod = (Mod) event.data;
					loader.modUpdated(mod);
				}
			});
			submitDownloadWorkflow(builder);
		} catch (IOException | UnsupportedHostException e) {
			throw new ModUpdateFailedError(e);
		}
	}
	
	public void downloadMod(URL url) throws ModUpdateFailedError, UnsupportedHostException {
		ModWorkflowBuilder builder = new ModWorkflowBuilder("Downloading " + FilenameUtils.getBaseName(url.toString()));
		try {
			builder.downloadMod(getCrawler(url), config, loader);
			builder.addListener(new TaskCallback.WorkflowCompleteCallback() {
				
				@Override
				protected void processTaskEvent(TaskEvent event) {
					Mod mod = (Mod) event.data;
					loader.modUpdated(mod);
				}
			});
			submitDownloadWorkflow(builder);
		} catch (IOException e) {
			throw new ModUpdateFailedError(e);
		}
	}
	
	public void addModZip(Path zipPath){
		ModWorkflowBuilder builder = new ModWorkflowBuilder("Adding " + zipPath);
		final Mod futureMod = builder.addLocalMod(zipPath, loader);
		builder.refreshModAfterWorkflowComplete(futureMod, loader);
		submitDownloadWorkflow(builder);
	}
	
	public void updateMods() throws ModUpdateFailedError, ModNotDownloadedException{
		for (Mod mod : loader.getMods()){
			updateMod(mod, false);
		}
	}
	
	public void toggleMod(final Mod mod) throws ModNotDownloadedException{
		ModWorkflowBuilder builder = new ModWorkflowBuilder("Toggling " + mod);
		if (loader.isEnabled(mod)){
			builder.disableMod(mod, loader);
		} else {
			builder.enableMod(mod, loader, config);
		}
		builder.refreshModAfterWorkflowComplete(mod, loader);
		
		submitEnablerWorkflow(builder);
	}
	
	public void deleteMod(final Mod mod) throws CannotDeleteModException {
		if (DefaultMods.isBuiltIn(mod)){
			throw new CannotDeleteModException(mod, "Built-in");
		}
		
		ModWorkflowBuilder builder = new ModWorkflowBuilder("Deleting " + mod);
		builder.deleteMod(mod, config, loader);
		builder.addListener(new TaskCallback.WorkflowCompleteCallback() {
			
			@Override
			protected void processTaskEvent(TaskEvent event) {
				loader.modDeleted(mod);
			}
		});
		
		submitEnablerWorkflow(builder);
	}
	
	public void checkForModUpdates() throws Exception{
		Exception e = null;
		
		for (final Mod mod : loader.getMods()){
			try {
				if (mod.isUpdateable()){
					ModWorkflowBuilder builder = new ModWorkflowBuilder("Checking for update for " + mod);
					builder.checkForUpdates(mod, getCrawler(mod));
					
					// If the Workflow completes with a success, mark the mod as having an update available 
					builder.addListener(new TaskCallback.WorkflowCompleteCallback() {
						
						@Override
						protected void processTaskEvent(TaskEvent event) {
							mod.updateAvailable = true;
							loader.modUpdated(mod);
						}
					});
					submitDownloadWorkflow(builder);
				}
			} catch (IOException | UnsupportedHostException ex) {
				ex.printStackTrace();
				e = ex;
			}
		}
		
		if (e != null){
			throw e;
		}
	}
	
	public void exportEnabledMods(Path path){
		loader.exportEnabledMods(path);
	}
	
	public void importMods(Path path){
		loader.importMods(path, this);
	}
	
	/**
	 * Launches an update task to check for the latest update to the Mod Manager.
	 * 
	 * If an update is available from Github, then the user is given a choice to update.
	 * @throws UnsupportedHostException 
	 */
	public void tryUpdateModManager() throws UnsupportedHostException {
		ModWorkflowBuilder builder = new ModWorkflowBuilder("Updating " + TinkerTime.NAME);
		try {
			builder.checkForUpdates(
				getCrawler(new URL(CrawlerFactory.APP_UPDATE_URL)),
				TinkerTime.VERSION, null
			);
			builder.addListener(new TaskCallback.WorkflowCompleteCallback() {
				
				@Override
				protected void processTaskEvent(TaskEvent event) {
					Crawler<?> crawler = (Crawler<?>) event.data;
					
					try {
						if (JOptionPane.showConfirmDialog(
							null,
							String.format(
								"%s v%s is available.%n" +
								"Would you like to download it?%n" +
								"%n" + 
								"You currently have v%s",
								TinkerTime.NAME, crawler.getMod().getVersion(), TinkerTime.VERSION
							),
							"Update Tinker Time",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE
						) == JOptionPane.YES_OPTION){
							BrowserGoToTask.callNow(crawler.getDownloadLink());
						}
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			});
			
			submitDownloadWorkflow(builder);
		} catch (MalformedURLException e) { /* Ignore */ }
	}
	
	public void openConfigWindow(){
		config.updateConfig();
		reloadMods();
	}
	
	private void reloadMods(){
		loader.init(this);  // Reload mods (top update views)
	}
	
	// -- Helpers -----------------------------------------------------------
	
	private Crawler<?> getCrawler(URL url) throws UnsupportedHostException{
		return crawlerFactory.getCrawler(url);
	}
	
	private Crawler<?> getCrawler(Mod mod) throws UnsupportedHostException{
		return getCrawler(mod.pageUrl);
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
