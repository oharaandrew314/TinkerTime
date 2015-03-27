package aohara.tinkertime.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.JOptionPane;

import org.apache.commons.io.FilenameUtils;

import aohara.common.selectorPanel.ListListener;
import aohara.common.workflows.ConflictResolver;
import aohara.common.workflows.ProgressPanel;
import aohara.common.workflows.tasks.BrowserGoToTask;
import aohara.common.workflows.tasks.TaskCallback;
import aohara.common.workflows.tasks.WorkflowBuilder;
import aohara.common.workflows.tasks.WorkflowTask.TaskEvent;
import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.TinkerTime;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.CrawlerFactory;
import aohara.tinkertime.crawlers.VersionInfo;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.crawlers.pageLoaders.JsonLoader;
import aohara.tinkertime.crawlers.pageLoaders.WebpageLoader;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.views.DialogConflictResolver;
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
public class ModManager implements ListListener<Mod> {
	
	public final TinkerConfig config;
	
	private final CrawlerFactory crawlerFactory;
	private final ThreadPoolExecutor downloadExecutor;
	private final Executor enablerExecutor;
	private final ModLoader loader;
	private final ProgressPanel progressPanel;
	private final ConflictResolver cr;
	
	private Mod selectedMod;
	
	public static ModManager createDefaultModManager(TinkerConfig config, ModLoader sm, ProgressPanel pp){
		return new ModManager(
			sm, config, pp, new DialogConflictResolver(),
			(ThreadPoolExecutor) Executors.newFixedThreadPool(config.numConcurrentDownloads()),
			Executors.newSingleThreadExecutor(),
			new CrawlerFactory(new WebpageLoader(), new JsonLoader())
		);
	}
	
	protected ModManager(
			ModLoader loader, TinkerConfig config, ProgressPanel progressPanel,
			ConflictResolver cr, ThreadPoolExecutor downloadExecutor,
			Executor enablerExecutor, CrawlerFactory crawlerFactory
	){
		this.loader = loader;
		this.config = config;
		this.progressPanel = progressPanel;
		this.cr = cr;
		this.downloadExecutor = downloadExecutor;
		this.enablerExecutor = enablerExecutor;
		this.crawlerFactory = crawlerFactory;
	}
	
	// -- Accessors --------------------------------------------------------
	
	public Mod getSelectedMod(){
		return selectedMod;
	}
	
	// -- Listeners -----------------------
	
	@Override
	public void elementClicked(Mod mod, int numTimes) throws Exception{
		if (numTimes == 2){
			toggleMod(mod);
		}
	}

	@Override
	public void elementSelected(Mod element) {
		selectedMod = element;
	}
	
	// -- Modifiers ---------------------------------
	
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
	 */
	public void updateMod(Mod mod, boolean forceUpdate) throws ModUpdateFailedError {
		if (mod.getPageUrl() == null){
			throw new ModUpdateFailedError(mod, "Mod is a local zip only, and cannot be updated.");
		}
		
		ModWorkflowBuilder builder = new ModWorkflowBuilder("Updating " + mod.getName());
		try {
			// Cleanup operations prior to update
			if (mod.isDownloaded(config)){
				if (!forceUpdate){
					builder.checkForUpdates(mod, getCrawler(mod));
				}
				
				if (mod.isEnabled(config)){
					builder.disableMod( mod, config, loader);
				}
				
				builder.deleteModZip(mod, config);
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
		builder.addLocalMod(zipPath, config, loader);
		builder.addListener(new TaskCallback.WorkflowCompleteCallback() {
			
			@Override
			protected void processTaskEvent(TaskEvent event) {
				Mod mod = (Mod) event.data;
				loader.modUpdated(mod);
			}
		});
		submitDownloadWorkflow(builder);
	}
	
	public void updateMods() throws ModUpdateFailedError{
		for (Mod mod : loader.getMods()){
			updateMod(mod, false);
		}
	}
	
	public void toggleMod(Mod mod) throws IOException{
		if (!mod.isDownloaded(config)){
			throw new ModNotDownloadedError(mod, "Cannot enable since not downloaded");
		}
		
		ModWorkflowBuilder builder = new ModWorkflowBuilder("Toggling " + mod);
		if (mod.isEnabled(config)){
			builder.disableMod(mod, config, loader);
		} else {
			builder.enableMod(mod, config, loader, cr);
		}
		
		submitEnablerWorkflow(builder);
	}
	
	public void deleteMod(final Mod mod) throws CannotDisableModError, IOException {
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
				if (mod.getPageUrl() != null){
					ModWorkflowBuilder builder = new ModWorkflowBuilder("Checking for update for " + mod);
					builder.checkForUpdates(mod, getCrawler(mod));
					
					// If the Workflow completes with a success, mark the mod as having an update available 
					builder.addListener(new TaskCallback.WorkflowCompleteCallback() {
						
						@Override
						protected void processTaskEvent(TaskEvent event) {
							mod.setUpdateAvailable();
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
	public void tryUpdateModManager() throws UnsupportedHostException{
		ModWorkflowBuilder builder = new ModWorkflowBuilder("Updating " + TinkerTime.NAME);
		VersionInfo currentVersion = new VersionInfo(TinkerTime.VERSION, null, TinkerTime.FULL_NAME);
		try {
			builder.checkForUpdates(
				getCrawler(new URL(CrawlerFactory.APP_UPDATE_URL)),
				currentVersion
			);
			builder.addListener(new TaskCallback.WorkflowCompleteCallback() {
				
				@Override
				protected void processTaskEvent(TaskEvent event) {
					Crawler<?> crawler = (Crawler<?>) event.data;
					
					try {
						if (JOptionPane.showConfirmDialog(
							null,
							String.format(
								"%s v%s is available.\n" +
								"Would you like to download it?\n" +
								"\n" + 
								"You currently have v%s",
								TinkerTime.NAME, crawler.getVersion(), TinkerTime.VERSION
							),
							"Update Tinker Time",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE
						) == JOptionPane.YES_OPTION){
							new BrowserGoToTask(crawler.getDownloadLink()).call(null);
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
		config.updateConfig(false);
		reloadMods();
	}
	
	private void reloadMods(){
		loader.init(this);  // Reload mods (top update views)
	}
	
	// -- Helpers -----------------------------------------------------------
	
	public Crawler<?> getCrawler(URL url) throws UnsupportedHostException{
		return crawlerFactory.getCrawler(url);
	}
	
	protected Crawler<?> getCrawler(Mod mod) throws UnsupportedHostException{
		return getCrawler(mod.getPageUrl());
	}
	
	// -- Exceptions/Errors --------------------------------------------------
	
	@SuppressWarnings("serial")
	public static class ModNotDownloadedError extends Error {
		private ModNotDownloadedError(Mod mod, String message){
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
}
