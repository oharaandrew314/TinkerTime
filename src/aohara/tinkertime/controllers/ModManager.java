package aohara.tinkertime.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.io.FilenameUtils;

import aohara.common.Listenable;
import aohara.common.selectorPanel.ListListener;
import aohara.common.workflows.ConflictResolver;
import aohara.common.workflows.ProgressPanel;
import aohara.common.workflows.Workflow;
import aohara.tinkertime.Config;
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
	
	public static final int NUM_CONCURRENT_DOWNLOADS = 4;
	
	private final Executor downloadExecutor, enablerExecutor;
	private final Config config;
	private final ModStateManager sm;
	private final ProgressPanel progressPanel;
	private final ConflictResolver cr;
	private Mod selectedMod;
	
	public static ModManager createDefaultModManager(Config config, ModStateManager sm, ProgressPanel pp){
		
		ModManager mm =  new ModManager(
			sm, config, pp, new DialogConflictResolver(),
			Executors.newFixedThreadPool(NUM_CONCURRENT_DOWNLOADS),
			Executors.newSingleThreadExecutor());
		
		return mm;
	}
	
	public ModManager(
			ModStateManager sm, Config config, ProgressPanel progressPanel,
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
		Workflow wf = new Workflow("Updating " + mod.getName());
		try {
			if (mod.isEnabled()){
				ModWorkflowBuilder.disableMod(wf, mod, config, sm);
			}
			ModWorkflowBuilder.downloadMod(wf, mod.getPageUrl(), config, sm);
			submitDownloadWorkflow(wf);
		} catch (IOException | UnsupportedHostException e) {
			throw new ModUpdateFailedException();
		}
	}
	
	public void downloadMod(URL url) throws ModUpdateFailedException, UnsupportedHostException {
		Workflow wf = new Workflow("Downloading " + FilenameUtils.getBaseName(url.toString()));
		try {
			ModWorkflowBuilder.downloadMod(wf, url, config, sm);
			submitDownloadWorkflow(wf);
		} catch (IOException e) {
			throw new ModUpdateFailedException();
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
		
		Workflow wf = new Workflow("Enabling " + mod);
		ModWorkflowBuilder.enableMod(wf, mod, config, sm, cr);		
		submitEnablerWorkflow(wf);
	}
	
	public void disableMod(Mod mod) throws ModAlreadyDisabledException, IOException {
		if (!mod.isEnabled()){
			throw new ModAlreadyDisabledException();
		}
		
		Workflow wf = new Workflow("Disabling " + mod);
		ModWorkflowBuilder.disableMod(wf, mod, config, sm);
		submitEnablerWorkflow(wf);
	}
	
	public void deleteMod(Mod mod) throws CannotDisableModException, IOException {
		Workflow wf = new Workflow("Deleting " + mod);
		ModWorkflowBuilder.deleteMod(wf, mod, config, sm);		
		submitEnablerWorkflow(wf);
	}
	
	public void checkForModUpdates() throws Exception{
		Exception e = null;
		
		for (Mod mod : sm.getMods()){
			try {
				Workflow wf = new Workflow("Checking for update for " + mod);
				ModWorkflowBuilder.checkForUpdates(wf, mod, mod, sm);
				submitDownloadWorkflow(wf);
			} catch (IOException | UnsupportedHostException ex) {
				ex.printStackTrace();
				e = ex;
			}
		}
		
		if (e != null){
			throw e;
		}
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
	public static class ModUpdateFailedException extends Exception {}
}
