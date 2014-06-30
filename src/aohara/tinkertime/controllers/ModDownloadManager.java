package aohara.tinkertime.controllers;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;

import aoahara.common.Listenable;
import aohara.tinkertime.config.Config;
import aohara.tinkertime.controllers.ModManager.CannotAddModException;
import aohara.tinkertime.controllers.ModManager.ModAlreadyUpToDateException;
import aohara.tinkertime.controllers.ModManager.ModUpdateFailedException;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModApi;
import aohara.tinkertime.models.ModPage;

public class ModDownloadManager extends Listenable<ModDownloadListener> {
	
	public static final int NUM_CONCURRENT_DOWNLOADS = 4;
	
	private final ExecutorService executor = (
		Executors.newFixedThreadPool(NUM_CONCURRENT_DOWNLOADS)
	);
	
	public void downloadMod(ModApi mod){
		executor.execute(new DownloadTask(mod));
	}
	
	private ModPage getNewPage(Mod mod) throws ModUpdateFailedException {
		try {
			return new ModPage(mod.getPageUrl());
		} catch (IOException e) {
			throw new ModUpdateFailedException();
		}
	}
	
	public boolean isUpdateAvailable(Mod mod){
		try {
			return isUpdateAvailable(mod, getNewPage(mod));
		} catch (ModUpdateFailedException e) {
			return false;
		}
	}
	
	public boolean isUpdateAvailable(Mod mod, ModPage page){
		System.err.println("mod " + mod.getUpdatedOn());
		System.err.println("page " + page.getUpdatedOn());
		return (page.getUpdatedOn().compareTo(mod.getUpdatedOn()) > 0);
	}
	
	
	public void tryUpdateData(Mod mod)
			throws ModAlreadyUpToDateException, ModUpdateFailedException,
			CannotAddModException {
		System.err.println("try update mod");
		ModPage page = getNewPage(mod);
		if (isUpdateAvailable(mod, page)){
			mod.updateModData(page);
		} else {
			throw new ModAlreadyUpToDateException();
		}
	}
	
	private class DownloadTask implements Runnable {
		
		private final ModApi mod;
		
		public DownloadTask(ModApi mod){
			this.mod = mod;
		}

		@Override
		public void run() {
			// Notify download start
			for (ModDownloadListener l : getListeners()){
				l.modDownloadStarted(mod);
			}
			
			try {
				FileUtils.copyURLToFile(
					mod.getDownloadLink(),
					new Config().getModZipPath(mod).toFile()
				);
				
				// Notify download complete
				for (ModDownloadListener l : getListeners()){
					l.modDownloadComplete(mod);
				}
			} catch (IOException e) {
				e.printStackTrace();
				// Notify download error
				for (ModDownloadListener l : getListeners()){
					l.modDownloadError(mod);
				}
			}
		}
		
	}
}
