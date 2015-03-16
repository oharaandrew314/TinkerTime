package aohara.tinkertime.controllers;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.io.FilenameUtils;

import aohara.common.Listenable;
import aohara.common.selectorPanel.ListListener;
import aohara.common.workflows.ConflictResolver;
import aohara.common.workflows.ProgressPanel;
import aohara.common.workflows.Workflow;
import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.CrawlerFactory;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.crawlers.pageLoaders.JsonLoader;
import aohara.tinkertime.crawlers.pageLoaders.WebpageLoader;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.views.DialogConflictResolver;
import aohara.tinkertime.workflows.ModWorkflowBuilder;
import aohara.tinkertime.workflows.contexts.AppUpdateContext;

/**
 * Controller for initiating Asynchronous Tasks for Mod Processing.
 * 
 * All Mod-Related Actions are to be initiated through this Controller.
 * All Asynchronous tasks initiated are executed by the executors of this class,
 * and the tasks are represented by {@link aohara.common.workflows.Workflow} classes.
 * 
 * @author Andrew O'Hara
 */
public class ModManager extends Listenable<ModUpdateListener> implements WorkflowRunner, ListListener<Mod> {
	
	private final CrawlerFactory crawlerFactory;
	private final Executor downloadExecutor, enablerExecutor;
	public final TinkerConfig config;
	private final ModLoader loader;
	private final ProgressPanel progressPanel;
	private final ConflictResolver cr;
	private Mod selectedMod;
	
	public static ModManager createDefaultModManager(TinkerConfig config, ModLoader sm, ProgressPanel pp){
		return new ModManager(
			sm, config, pp, new DialogConflictResolver(),
			Executors.newFixedThreadPool(config.numConcurrentDownloads()),
			Executors.newSingleThreadExecutor(),
			new CrawlerFactory(new WebpageLoader(), new JsonLoader())
		);
	}
	
	public ModManager(
			ModLoader loader, TinkerConfig config, ProgressPanel progressPanel,
			ConflictResolver cr, Executor downloadExecutor,
			Executor enablerExecutor, CrawlerFactory crawlerFactory
	){
		this.loader = loader;
		this.config = config;
		this.progressPanel = progressPanel;
		this.cr = cr;
		this.downloadExecutor = downloadExecutor;
		this.enablerExecutor = enablerExecutor;
		this.crawlerFactory = crawlerFactory;
		
		addListener(loader);
	}
	
	// -- Accessors --------------------------------------------------------
	
	public Mod getSelectedMod(){
		return selectedMod;
	}
	
	// -- Listeners -----------------------
	
	@Override
	public void elementClicked(Mod mod, int numTimes) throws Exception{
		if (numTimes == 2){
			if (mod.isEnabled()){
				disableMod(mod);
			} else {
				enableMod(mod);
			}
		}
	}

	@Override
	public void elementSelected(Mod element) {
		selectedMod = element;
	}
	
	// -- Modifiers ---------------------------------
	
	@Override
	public void submitDownloadWorkflow(Workflow workflow){
		workflow.addListener(progressPanel);
		downloadExecutor.execute(workflow);
	}
	
	@Override
	public void submitEnablerWorkflow(Workflow workflow){
		workflow.addListener(progressPanel);
		enablerExecutor.execute(workflow);
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
					builder.checkForUpdates(mod, getCrawler(mod), mod, loader);
				}
				
				if (mod.isEnabled()){
					builder.disableMod( mod, config, loader);
				}
				
				builder.deleteModZip(mod, config);
			}
			builder.downloadMod(getCrawler(mod), config, loader);
			submitDownloadWorkflow(builder.buildWorkflow());
		} catch (IOException | UnsupportedHostException e) {
			throw new ModUpdateFailedError(e);
		}
	}
	
	public void downloadMod(URL url) throws ModUpdateFailedError, UnsupportedHostException {
		ModWorkflowBuilder builder = new ModWorkflowBuilder("Downloading " + FilenameUtils.getBaseName(url.toString()));
		try {
			builder.downloadMod(getCrawler(url), config, loader);
			submitDownloadWorkflow(builder.buildWorkflow());
		} catch (IOException e) {
			throw new ModUpdateFailedError(e);
		}
	}
	
	public void addModZip(Path zipPath){
		ModWorkflowBuilder builder = new ModWorkflowBuilder("Adding " + zipPath);
		builder.addLocalMod(zipPath, config, loader);
		submitDownloadWorkflow(builder.buildWorkflow());
	}
	
	public void updateMods() throws ModUpdateFailedError{
		for (Mod mod : loader.getMods()){
			updateMod(mod, false);
		}
	}
	
	public void enableMod(Mod mod) throws ModAlreadyEnabledError, ModNotDownloadedError, IOException {
		if (mod.isEnabled()){
			throw new ModAlreadyEnabledError();
		} else if (!mod.isDownloaded(config)){
			throw new ModNotDownloadedError(mod, "Cannot enable since not downloaded");
		}
		
		ModWorkflowBuilder builder = new ModWorkflowBuilder("Enabling " + mod);
		builder.enableMod(mod, config, loader, cr);
		submitEnablerWorkflow(builder.buildWorkflow());
	}
	
	public void disableMod(Mod mod) throws ModAlreadyDisabledError, IOException {
		if (!mod.isEnabled()){
			throw new ModAlreadyDisabledError();
		}
		
		ModWorkflowBuilder builder = new ModWorkflowBuilder("Disabling " + mod);
		builder.disableMod(mod, config, loader);
		submitEnablerWorkflow(builder.buildWorkflow());
	}
	
	public void deleteMod(Mod mod) throws CannotDisableModError, IOException {
		ModWorkflowBuilder builder = new ModWorkflowBuilder("Deleting " + mod);
		builder.deleteMod(mod, config, loader);
		submitEnablerWorkflow(builder.buildWorkflow());
	}
	
	public void checkForModUpdates() throws Exception{
		Exception e = null;
		
		for (Mod mod : loader.getMods()){
			try {
				if (mod.getPageUrl() != null){
					ModWorkflowBuilder builder = new ModWorkflowBuilder("Checking for update for " + mod);
					builder.checkForUpdates(mod, getCrawler(mod), mod, loader);
					submitDownloadWorkflow(builder.buildWorkflow());
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
		AppUpdateContext.checkForUpdates(this);
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
	public static class ModAlreadyEnabledError extends Error {}
	@SuppressWarnings("serial")
	public static class ModAlreadyDisabledError extends Error {}
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
