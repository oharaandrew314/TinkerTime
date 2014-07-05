package aohara.tinkertime.models;

import java.io.IOException;
import java.nio.file.Path;

import aohara.common.executors.context.FileTransferContext;
import aohara.tinkertime.controllers.ModManager.CannotAddModException;

public class PageDownloadContext extends FileTransferContext {
	
	public final Mod mod;

	public PageDownloadContext(Mod mod, Path tempPath) {
		super(mod.getPageUrl(), tempPath);
		this.mod = mod;
		tempPath.toFile().deleteOnExit();
	}

	@Override
	public int getTotalProgress() {
		try {
			return mod.getDownloadLink().openConnection().getContentLength();
		} catch (IOException e) {
			return -1;
		}
	}

	@Override
	public String toString() {
		return mod.getName();
	}
	
	public ModPage getPage() throws CannotAddModException{
		return ModPage.createFromFile(getResult(), getSubject());
	}
	
	public boolean isUpdateAvailable(){
		try {
			return getPage().getUpdatedOn().compareTo(mod.getUpdatedOn()) > 0;
		} catch (CannotAddModException e) {
			return false;
		}
	}
}
