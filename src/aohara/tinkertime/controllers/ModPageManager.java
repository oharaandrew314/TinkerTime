package aohara.tinkertime.controllers;

import java.nio.file.Path;

import aohara.common.Listenable;
import aohara.common.progressDialog.ProgressListener;
import aohara.tinkertime.controllers.ModManager.CannotAddModException;
import aohara.tinkertime.controllers.ModManager.ModUpdateFailedException;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModPage;

public class ModPageManager extends
		Listenable<ProgressListener<Path>> {
	
	public static final int NUM_CONCURRENT_DOWNLOADS = 4;
	
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
	
	private boolean isUpdateAvailable(Mod mod, ModPage page){
		return (page.getUpdatedOn().compareTo(mod.getUpdatedOn()) > 0);
	}
}
