package aohara.tinkertime.controllers;

import java.net.MalformedURLException;
import java.net.URL;

import aohara.common.Listenable;
import aohara.common.executors.Downloader;
import aohara.common.progressDialog.ProgressListener;
import aohara.tinkertime.config.Config;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModApi;
import aohara.tinkertime.models.ModEnableContext;
import aohara.tinkertime.models.ModEnableContext.EnableAction;
import aohara.tinkertime.models.ModPage;

public class ModManager extends Listenable<ModUpdateListener> implements ProgressListener<ModEnableContext> {
	
	public static final int NUM_CONCURRENT_DOWNLOADS = 4;
	
	private final Downloader downloader;
	private final ModEnabler enabler;
	private final Config config;
	
	public ModManager(
			ModStateManager sm, Config config, Downloader downloader,
			ModEnabler enabler){
		this.config = config;
		this.downloader = downloader;
		this.enabler = enabler;
		
		addListener(sm);
		enabler.addListener(this);
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
	
	public boolean isDownloaded(ModApi mod){
		return isDownloaded(mod, config);
	}
	
	// -- Modifiers ---------------------------------
	
	public Mod addNewMod(String url) throws CannotAddModException{
		try {
			return addNewMod(ModPage.createFromUrl(new URL(url)));
		} catch (MalformedURLException e) {
			throw new CannotAddModException();
		}
	}
	
	public Mod addNewMod(ModPage modPage) throws CannotAddModException, CannotAddModException {
		Mod mod = new Mod(modPage);
		downloader.download(mod.getDownloadLink(), config.getModZipPath(mod));
		notifyModUpdated(mod, false);
		return mod;
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
	
	// -- Listeners ------------------------------------------------------
	
	@Override
	public void progressStarted(ModEnableContext object, int target,
			int tasksRunning) { /* */}
	@Override
	public void progressMade(ModEnableContext object, int current) { /* */}
	@Override
	public void progressError(ModEnableContext object, int tasksRunning) { /* */}
	
	@Override
	public void progressComplete(ModEnableContext context, int tasksRunning) {
		if (context.isSuccessful()){
			context.mod.setEnabled(context.action == EnableAction.Enable);
			notifyModUpdated(context.mod, context.action == EnableAction.Delete);
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
