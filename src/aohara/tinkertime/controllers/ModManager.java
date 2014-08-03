package aohara.tinkertime.controllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import aohara.common.Listenable;
import aohara.common.executors.context.ExecutorContext;
import aohara.common.executors.progress.ProgressListener;
import aohara.common.workflows.ProgressPanel;
import aohara.common.workflows.Workflow;
import aohara.tinkertime.config.Config;
import aohara.tinkertime.controllers.files.ModEnabler;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.context.ModEnableContext;
import aohara.tinkertime.models.context.ModEnableContext.EnableAction;
import aohara.tinkertime.workflows.CheckForUpdateWorkflow;
import aohara.tinkertime.workflows.UpdateModWorkflow;

public class ModManager extends Listenable<ModUpdateListener>
		implements ProgressListener {
	
	public static final int NUM_CONCURRENT_DOWNLOADS = 4;
	
	private final Executor executor = Executors.newFixedThreadPool(NUM_CONCURRENT_DOWNLOADS);
	private final ModEnabler enabler;
	private final Config config;
	private final ModStateManager sm;
	private final ProgressPanel progressPanel;
	
	public ModManager(
			ModStateManager sm, Config config, ModEnabler enabler,
			ProgressPanel progressPanel){
		this.sm = sm;
		this.config = config;
		this.enabler = enabler;
		this.progressPanel = progressPanel;
		
		this.addListener(sm);
		enabler.addListener(this);
	}
	
	// -- Listeners -----------------------
	
	public void notifyModUpdated(Mod mod, boolean deleted){
		for (ModUpdateListener l : getListeners()){
			l.modUpdated(mod, deleted);
		}
	}
	
	// -- Accessors ------------------------
	
	public static boolean isDownloaded(Mod mod, Config config){
		return config.getModZipPath(mod).toFile().exists();
	}
	
	public boolean isDownloaded(Mod mod){
		return isDownloaded(mod, config);
	}
	
	// -- Modifiers ---------------------------------
	
	private void submitWorkflow(Workflow workflow){
		workflow.addListener(progressPanel);
		executor.execute(workflow);
	}
	
	public void addNewMod(String url) throws CannotAddModException {
		try {
			submitWorkflow(new UpdateModWorkflow(new URL(url), config, sm));
		} catch (MalformedURLException e) {
			throw new CannotAddModException();
		}
	}
	
	public void updateMod(Mod mod) throws ModUpdateFailedException {
		// TODO Merge with addNewMod
		try {
			addNewMod(mod.getPageUrl().toString());
		} catch (CannotAddModException e) {
			throw new ModUpdateFailedException();
		}
	}
	
	public void updateMods() throws ModUpdateFailedException{
		boolean error = false;
		for (Mod mod : sm.getMods()){
			try {
				updateMod(mod);
			} catch (ModUpdateFailedException e) {
				error = true;
			}
		}
		
		if (error){
			throw new ModUpdateFailedException();
		}
	}
	
	public void enableMod(Mod mod)
		throws ModAlreadyEnabledException, ModNotDownloadedException,
		CannotEnableModException, CannotDisableModException
	{
		if (mod.isEnabled()){
			throw new ModAlreadyEnabledException();
		} else if (!isDownloaded(mod)){
			throw new ModNotDownloadedException();
		}
		
		enabler.enable(mod, config);
	}
	
	public void disableMod(Mod mod)
			throws ModAlreadyDisabledException, CannotDisableModException {
		if (!mod.isEnabled()){
			throw new ModAlreadyDisabledException();
		}
		
		enabler.disable(mod, config);
	}
	
	public void deleteMod(Mod mod) throws CannotDisableModException {
		enabler.delete(mod, config);
	}
	
	public void checkForUpdates() throws ModUpdateFailedException {	
		for (Mod mod : sm.getMods()){
			submitWorkflow(new CheckForUpdateWorkflow(mod, sm));
		}
	}
	
	// -- Listeners ------------------------------------------------------
	
	@Override
	public void progressStarted(ExecutorContext object, int target,
			int tasksRunning) { /* */}
	@Override
	public void progressMade(ExecutorContext object, int current) { /* */}
	@Override
	public void progressError(ExecutorContext object, int tasksRunning) { /* */}
	
	@Override
	public void progressComplete(ExecutorContext ctx, int tasksRunning) {
		if (ctx instanceof ModEnableContext){
			ModEnableContext context = (ModEnableContext) ctx;
			if (ctx.isSuccessful()){
				EnableAction action = context.action;
				context.mod.setEnabled(action == EnableAction.Enable);
				notifyModUpdated(context.mod, action == EnableAction.Delete);
			}
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
	@SuppressWarnings("serial")
	public static class ModAlreadyUpToDateException extends Exception {}
}
