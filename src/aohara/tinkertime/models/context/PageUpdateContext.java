package aohara.tinkertime.models.context;

import java.nio.file.Path;

import aohara.tinkertime.controllers.ModManager.CannotAddModException;
import aohara.tinkertime.models.DownloadedFile;
import aohara.tinkertime.models.pages.CurseModPage;

public class PageUpdateContext extends PageDownloadContext {
	
	private final DownloadedFile file;
	private final boolean updateAfter;
	
	public PageUpdateContext(DownloadedFile file, Path tempPath, boolean updateAfter){
		super(file.getPageUrl(), tempPath);
		this.file = file;
		this.updateAfter = updateAfter;
	}
	
	@Override
	public String toString(){
		return file.getNewestFileName();
	}
	
	public boolean isUpdateAvailable(){
		try {
			return getPage().getUpdatedOn().compareTo(file.getUpdatedOn()) > 0;
		} catch (CannotAddModException e) {
			return false;
		}
	}
	
	public boolean downloadIfAvailable(){
		return updateAfter;
	}

	@Override
	public CurseModPage getPage() throws CannotAddModException {
		return CurseModPage.createFromFile(getDest(), getSource());
	}

}
