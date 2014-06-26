package aohara.tinkertime.controllers;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;

import aoahara.common.Listenable;
import aohara.tinkertime.models.ModApi;

public class DownloadManager extends Listenable<ModDownloadListener> {
	
	public static final int NUM_CONCURRENT_DOWNLOADS = 4;
	
	private final ExecutorService executor = (
		Executors.newFixedThreadPool(NUM_CONCURRENT_DOWNLOADS)
	);
	
	public void downloadMod(ModApi mod){
		executor.execute(new DownloadTask(mod));
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
					ModManager.modZipPath(mod).toFile()
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
