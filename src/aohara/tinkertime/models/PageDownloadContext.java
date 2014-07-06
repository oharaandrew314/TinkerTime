package aohara.tinkertime.models;

import java.io.IOException;
import java.nio.file.Path;

import aohara.common.executors.context.FileTransferContext;
import aohara.tinkertime.controllers.ModManager.CannotAddModException;

public class PageDownloadContext extends FileTransferContext {
	
	public final Mod mod;
	public final boolean updateAfter;

	public PageDownloadContext(Mod mod, Path tempPath, boolean updateAfter) {
		super(mod.getPageUrl(), tempPath);
		this.mod = mod;
		this.updateAfter = updateAfter;
		tempPath.toFile().deleteOnExit();
	}

	@Override
	public int getTotalProgress() {
		try {
			return mod.getPageUrl().openConnection().getContentLength();
		} catch (IOException e) {
			return -1;
		}
	}

	@Override
	public String toString() {
		return mod.getName();
	}
	
	public ModPage getPage() throws CannotAddModException{
		return ModPage.createFromFile(getDest(), getSource());
	}
	
	public boolean isUpdateAvailable(){
		try {
			return getPage().getUpdatedOn().compareTo(mod.getUpdatedOn()) > 0;
		} catch (CannotAddModException e) {
			return false;
		}
	}
}
