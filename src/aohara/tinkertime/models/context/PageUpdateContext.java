package aohara.tinkertime.models.context;

import java.nio.file.Path;

import aohara.tinkertime.controllers.ModManager.CannotAddModException;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModPage;

public class PageUpdateContext extends PageDownloadContext {
	
	public final Mod mod;
	private final boolean updateAfter;
	
	public PageUpdateContext(Mod mod, Path tempPath, boolean updateAfter){
		super(mod.getPageUrl(), tempPath);
		this.mod = mod;
		this.updateAfter = updateAfter;
	}
	
	@Override
	public String toString(){
		return mod.getName();
	}
	
	public boolean isUpdateAvailable(){
		try {
			return getPage().getUpdatedOn().compareTo(mod.getUpdatedOn()) > 0;
		} catch (CannotAddModException e) {
			return false;
		}
	}
	
	public boolean downloadIfAvailable(){
		return updateAfter;
	}

	@Override
	public ModPage getPage() throws CannotAddModException {
		return ModPage.createFromFile(getDest(), getSource());
	}

}
