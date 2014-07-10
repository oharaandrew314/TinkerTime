package aohara.tinkertime.controllers;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import aohara.common.Listenable;
import aohara.common.executors.Downloader;
import aohara.common.executors.context.ExecutorContext;
import aohara.common.executors.progress.ProgressListener;
import aohara.tinkertime.config.Config;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModApi;
import aohara.tinkertime.models.ModEnableContext;
import aohara.tinkertime.models.ModEnableContext.EnableAction;
import aohara.tinkertime.models.ModPage;
import aohara.tinkertime.models.context.DownloadModContext;
import aohara.tinkertime.models.context.NewModPageContext;
import aohara.tinkertime.models.context.PageUpdateContext;

public class ModManager extends Listenable<ModUpdateListener>
		implements ProgressListener {
	
	public static final int NUM_CONCURRENT_DOWNLOADS = 4;
	
	private final Downloader pageDownloader, modDownloader;
	private final ModEnabler enabler;
	private final Config config;
	private final ModStateManager sm;
	
	public ModManager(
			ModStateManager sm, Config config, Downloader pageDownloader,
			Downloader modDownloader, ModEnabler enabler){
		this.sm = sm;
		this.config = config;
		this.pageDownloader = pageDownloader;
		this.modDownloader = modDownloader;
		this.enabler = enabler;
		
		this.addListener(sm);
		enabler.addListener(this);
		pageDownloader.addListener(this);
		modDownloader.addListener(this);
	}
	
	// -- Listeners -----------------------
	
	public void notifyModUpdated(Mod mod, boolean deleted){
		for (ModUpdateListener l : getListeners()){
			l.modUpdated(mod, deleted);
		}
	}
	
	// -- Accessors ------------------------
	
	public static boolean isDownloaded(ModApi mod, Config config){
		return config.getModZipPath(mod).toFile().exists();
	}
	
	private boolean isDownloaded(ModApi mod){
		return isDownloaded(mod, config);
	}
	
	private Path createTempFile() throws IOException{
		return Files.createTempFile("download", ".temp");
	}
	
	// -- Modifiers ---------------------------------
	
	public void addNewMod(String url) throws CannotAddModException {
		try {
			modDownloader.submit(new NewModPageContext(new URL(url), createTempFile()));
		} catch (IOException e) {
			throw new CannotAddModException();
		}
	}
	
	public void updateMod(Mod mod) throws ModUpdateFailedException {
		try {
			pageDownloader.submit(new PageUpdateContext(mod, createTempFile(), true));
		} catch (IOException e) {
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
		boolean error = false;
		for (Mod mod : sm.getMods()){
			try {
				Path tempFile = Files.createTempFile("download", ".temp");
				pageDownloader.submit(new PageUpdateContext(mod, tempFile, false));
			} catch (IOException e) {
				error = true;
			}
		}
		
		// If an error occured during execution, re-throw exception
		if (error){
			throw new ModUpdateFailedException();
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
		if (ctx instanceof DownloadModContext){
			DownloadModContext context = (DownloadModContext) ctx;
			try {
				notifyModUpdated(new Mod(context.modApi), false);
			} catch (CannotAddModException e) {
				// TODO Send result back to GUI
				e.printStackTrace();
			}
		}
		if (ctx instanceof ModEnableContext){
			ModEnableContext context = (ModEnableContext) ctx;
			if (ctx.isSuccessful()){
				EnableAction action = context.action;
				context.mod.setEnabled(action == EnableAction.Enable);
				notifyModUpdated(context.mod, action == EnableAction.Delete);
			}
		}
		else if (ctx instanceof NewModPageContext){
			NewModPageContext context = (NewModPageContext) ctx;
			try {
				modDownloader.submit(new DownloadModContext(context.getPage(), config));
			} catch (CannotAddModException e) {
				// TODO Send Result back to GUI
				e.printStackTrace();
			}
			
		}
		else if (ctx instanceof PageUpdateContext){
			PageUpdateContext context = (PageUpdateContext) ctx;
			Mod mod = context.mod;
			if (context.isUpdateAvailable() || !isDownloaded(mod, config)){
				mod.setUpdateAvailable();
				notifyModUpdated(mod, false);
				
				// If download requested, download mod
				if (context.downloadIfAvailable()){
					try {
						ModPage page = context.getPage();
						mod.updateModData(page);
						modDownloader.submit(new DownloadModContext(mod, config));
					} catch (CannotAddModException e) {
						// TODO Send result back to GUI
						e.printStackTrace();
					}
				}
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
