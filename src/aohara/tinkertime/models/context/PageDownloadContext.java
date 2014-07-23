package aohara.tinkertime.models.context;

import java.net.URL;
import java.nio.file.Path;

import aohara.common.executors.context.FileTransferContext;
import aohara.tinkertime.controllers.ModManager.CannotAddModException;
import aohara.tinkertime.models.pages.ModPage;

public abstract class PageDownloadContext extends FileTransferContext {

	public PageDownloadContext(URL pageUrl, Path tempPath) {
		super(pageUrl, tempPath);
		tempPath.toFile().deleteOnExit();
	}

	@Override
	public abstract String toString();
	
	public abstract ModPage getPage() throws CannotAddModException;
	public abstract boolean isUpdateAvailable();
}
