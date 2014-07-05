package aohara.tinkertime.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;

import aohara.common.executors.Downloader;
import aohara.common.progressDialog.ProgressListener;
import aohara.tinkertime.controllers.ModManager.CannotAddModException;
import aohara.tinkertime.controllers.ModManager.ModUpdateFailedException;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModPage;
import aohara.tinkertime.models.PageDownloadContext;

public class ModPageDownloader extends Downloader implements ProgressListener<Path> {
	
	public static final int NUM_CONCURRENT_DOWNLOADS = 4;
	private final Collection<PageDownloadContext> downloads = new LinkedList<>();
	private final ModStateManager sm;
	
	public ModPageDownloader(ModStateManager sm){
		super(NUM_CONCURRENT_DOWNLOADS);
		addListener(this);
		this.sm = sm;
	}
	
	private ModPage getNewPage(Mod mod) throws ModUpdateFailedException {
		try {
			return ModPage.createFromUrl(mod.getPageUrl());
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
	
	public boolean tryUpdateData(Mod mod) throws ModUpdateFailedException, CannotAddModException{
		ModPage page = getNewPage(mod);
		if (isUpdateAvailable(mod, page)){
			mod.updateModData(page);
			return true;
		}
		return false;
	}
	
	public void checkForUpdates(ModManager mm, Collection<Mod> mods) throws IOException {
		synchronized(downloads){
			for (Mod mod : mods){
				Path tempPath = Files.createTempFile("temp", ".download");
				PageDownloadContext context = new PageDownloadContext(mod, tempPath);
				FileTask fileTask = new FileTask(context);
				submit(fileTask);
				downloads.add(context);
			}
		}
	}
	
	private boolean isUpdateAvailable(Mod mod, ModPage page){
		return (page.getUpdatedOn().compareTo(mod.getUpdatedOn()) > 0);
	}

	@Override
	public void progressStarted(Path object, int target, int tasksRunning) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void progressMade(Path object, int current) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void progressComplete(Path object, int tasksRunning) {
		synchronized(downloads){
			if (tasksRunning == 0){
				for (PageDownloadContext context : downloads){
					if (context.isUpdateAvailable()){
						context.mod.setUpdateAvailable();
						sm.modUpdated(context.mod, false);
					}
				}
			}
		}
		
	}

	@Override
	public void progressError(Path object, int tasksRunning) {
		// TODO Auto-generated method stub
		
	}
}
