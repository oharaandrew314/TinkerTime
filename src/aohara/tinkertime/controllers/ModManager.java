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
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
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
public class ModManager extends Listenable<ModUpdateListener> implements WorkflowRunner, ListListener<Mod> {
	
	private final Executor downloadExecutor, enablerExecutor;
	public final TinkerConfig config;
	private final ModStateManager sm;
	private final ProgressPanel progressPanel;
	private final ConflictResolver cr;
	private Mod selectedMod;
	
	public static ModManager createDefaultModManager(TinkerConfig config, ModStateManager sm, ProgressPanel pp){
		
		ModManager mm =  new ModManager(
			sm, config, pp, new DialogConflictResolver(),
			Executors.newFixedThreadPool(config.numConcurrentDownloads()),
			Executors.newSingleThreadExecutor());
		
		return mm;
	}
	
	public ModManager(
			ModStateManager sm, TinkerConfig config, ProgressPanel progressPanel,
			ConflictResolver cr, Executor downloadExecutor,
			Executor enablerExecutor){
		this.sm = sm;
		this.config = config;
		this.progressPanel = progressPanel;
		this.cr = cr;
		this.downloadExecutor = downloadExecutor;
		this.enablerExecutor = enablerExecutor;
		
		addListener(sm);
	}
	
	// -- Accessors --------------------------------------------------------
	
	public Mod getSelectedMod(){
		return selectedMod;
	}
	
	// -- Listeners -----------------------
	
	public void notifyModUpdated(Mod mod, boolean deleted){
		for (ModUpdateListener l : getListeners()){
			l.modUpdated(mod, deleted);
		}
	}
	
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
	
	public void updateMod(Mod mod) throws ModUpdateFailedException {
		ModWorkflowBuilder builder = new ModWorkflowBuilder("Updating " + mod.getName());
		try {
			if (mod.isEnabled() && mod.isDownloaded(config)){
				builder.disableMod( mod, config, sm);
			}
			builder.downloadMod(mod.getPageUrl(), config, sm);
			submitDownloadWorkflow(builder.buildWorkflow());
		} catch (IOException | UnsupportedHostException e) {
			throw new ModUpdateFailedException(e);
		}
	}
	
	public void downloadMod(URL url) throws ModUpdateFailedException, UnsupportedHostException {
		ModWorkflowBuilder builder = new ModWorkflowBuilder("Downloading " + FilenameUtils.getBaseName(url.toString()));
		try {
			builder.downloadMod(url, config, sm);
			submitDownloadWorkflow(builder.buildWorkflow());
		} catch (IOException e) {
			throw new ModUpdateFailedException(e);
		}
	}
	
	public void updateMods() throws ModUpdateFailedException{
		for (Mod mod : sm.getMods()){
			updateMod(mod);
		}
	}
	
	public void enableMod(Mod mod) throws ModAlreadyEnabledException, ModNotDownloadedException, IOException {
		if (mod.isEnabled()){
			throw new ModAlreadyEnabledException();
		} else if (!mod.isDownloaded(config)){
			throw new ModNotDownloadedException();
		}
		
		ModWorkflowBuilder builder = new ModWorkflowBuilder("Enabling " + mod);
		builder.enableMod(mod, config, sm, cr);
		submitEnablerWorkflow(builder.buildWorkflow());
	}
	
	public void disableMod(Mod mod) throws ModAlreadyDisabledException, IOException {
		if (!mod.isEnabled()){
			throw new ModAlreadyDisabledException();
		}
		
		ModWorkflowBuilder builder = new ModWorkflowBuilder("Disabling " + mod);
		builder.disableMod(mod, config, sm);
		submitEnablerWorkflow(builder.buildWorkflow());
	}
	
	public void deleteMod(Mod mod) throws CannotDisableModException, IOException {
		ModWorkflowBuilder builder = new ModWorkflowBuilder("Deleting " + mod);
		builder.deleteMod(mod, config, sm);		
		submitEnablerWorkflow(builder.buildWorkflow());
	}
	
	public void checkForModUpdates() throws Exception{
		Exception e = null;
		
		for (Mod mod : sm.getMods()){
			try {
				ModWorkflowBuilder builder = new ModWorkflowBuilder("Checking for update for " + mod);
				builder.checkForUpdates(mod, mod, sm);
				submitDownloadWorkflow(builder.buildWorkflow());
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
		sm.exportEnabledMods(path);
	}
	
	// -- Exceptions ------------------------------------------------------
	
	@SuppressWarnings("serial")
	public static class CannotAddModException extends Exception {}
	@SuppressWarnings("serial")
	public static class ModAlreadyEnabledException extends Exception {}
	@SuppressWarnings("serial")
	public static class ModAlreadyDisabledException extends Exception {}
	@SuppressWarnings("serial")
	public static class ModNotDownloadedException extends Exception {}
	@SuppressWarnings("serial")
	public static class CannotDisableModException extends Exception {}
	@SuppressWarnings("serial")
	public static class CannotEnableModException extends Exception {}
	@SuppressWarnings("serial")
	public static class ModUpdateFailedException extends Exception {
		public ModUpdateFailedException(Exception e){
			super(e);
		}
	}
}
