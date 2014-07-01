package aohara.tinkertime.controllers;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;

import aoahara.common.Listenable;
import aohara.tinkertime.config.Config;
import aohara.tinkertime.controllers.ModManager.CannotAddModException;
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
			return ModPage.getLatestPage(mod);
		} catch (CannotAddModException e) {
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
		return (page.getUpdatedOn().compareTo(mod.getUpdatedOn()) > 0);
	}
	
	
	public boolean tryUpdateData(Mod mod) throws ModUpdateFailedException, CannotAddModException{
		ModPage page = getNewPage(mod);
		if (isUpdateAvailable(mod, page)){
			mod.updateModData(page);
			return true;
		}
		return false;
	}
	
	private class DownloadTask implements Runnable {
		
		private int downloads = 0;
		
		private final ModApi mod;
		
		public DownloadTask(ModApi mod){
			this.mod = mod;
		}

		@Override
		public void run() {
			boolean error = false;
			downloads++;
			
			// Notify download start
			for (ModDownloadListener l : getListeners()){
				l.modDownloadStarted(mod, downloads);
			}
			
			try {
				FileUtils.copyURLToFile(
					mod.getDownloadLink(),
					new Config().getModZipPath(mod).toFile()
				);
			} catch (IOException e) {
				error = true;
			}
			
			// Notify of download result
			downloads--;
			for (ModDownloadListener l : getListeners()){
				if (error){
					l.modDownloadError(mod, downloads);
				} else {
					l.modDownloadComplete(mod, downloads);
				}
			}
		}
		
	}
}
