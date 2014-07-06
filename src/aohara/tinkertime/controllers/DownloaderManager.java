package aohara.tinkertime.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import aohara.common.executors.Downloader;
import aohara.common.executors.FileTransferExecutor.FileTask;
import aohara.common.executors.context.FileTransferContext;
import aohara.common.progressDialog.ProgressListener;
import aohara.tinkertime.config.Config;
import aohara.tinkertime.controllers.ModManager.CannotAddModException;
import aohara.tinkertime.controllers.ModManager.ModUpdateFailedException;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.PageDownloadContext;

public class DownloaderManager implements ProgressListener<FileTransferContext> {
	
	public static final int NUM_CONCURRENT_DOWNLOADS = 4;
	private final ModStateManager sm;
	private final Downloader modDownloader;
	private final Config config;
	
	public DownloaderManager(ModStateManager sm, Downloader modDownloader, Config config){
		modDownloader.addListener(this);
		this.sm = sm;
		this.modDownloader = modDownloader;
		this.config = config;
	}
	
	public void checkForUpdates(ModManager mm, Collection<Mod> mods) throws ModUpdateFailedException {	
		ModUpdateFailedException e = null;
		for (Mod mod : mods){
			try {
				submitUpdateTask(mod, false);
			} catch (ModUpdateFailedException e2) {
				e = e2;
			}
		}
		
		// If an error occured during execution, re-throw exception
		if (e != null){
			throw e;
		}
	}
	
	private void submitUpdateTask(Mod mod, boolean updateModAfter) throws ModUpdateFailedException{
		try {
			Path tempPath = Files.createTempFile("temp", ".download");
			PageDownloadContext context = new PageDownloadContext(mod, tempPath, updateModAfter);
			FileTask fileTask = modDownloader.new FileTask(context);
			modDownloader.submit(fileTask);
		} catch (IOException e) {
			throw new ModUpdateFailedException();
		}
	}
	
	public void updateMod(Mod mod) throws ModUpdateFailedException {
		submitUpdateTask(mod, true);
	}

	@Override
	public void progressComplete(FileTransferContext context, int tasksRunning) {
		if (context instanceof PageDownloadContext){
			PageDownloadContext pageContext = (PageDownloadContext) context;
			Mod mod = pageContext.mod;
			if (pageContext.isUpdateAvailable() || !ModManager.isDownloaded(mod, config)){
				mod.setUpdateAvailable();
				sm.modUpdated(mod, false);
				
				// If context requires update, perform the update
				if (pageContext.updateAfter){
					try {
						mod.updateModData(pageContext.getPage());
						modDownloader.download(mod.getDownloadLink(), config.getModZipPath(mod));
					} catch (CannotAddModException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void progressStarted(
			FileTransferContext object, int target, int tasksRunning) { /* */ }
	@Override
	public void progressMade(FileTransferContext object, int current) { /* */ }
	@Override
	public void progressError(FileTransferContext object, int tasksRunning) { /* */ }
}
