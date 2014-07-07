package aohara.tinkertime.models.context;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import aohara.common.executors.context.FileTransferContext;
import aohara.tinkertime.controllers.ModManager.CannotAddModException;
import aohara.tinkertime.models.ModPage;

public abstract class PageDownloadContext extends FileTransferContext {
	
	private final URL pageUrl;

	public PageDownloadContext(URL pageUrl, Path tempPath) {
		super(pageUrl, tempPath);
		this.pageUrl = pageUrl;
		tempPath.toFile().deleteOnExit();
	}

	@Override
	public int getTotalProgress() {
		try {
			return pageUrl.openConnection().getContentLength();
		} catch (IOException e) {
			return -1;
		}
	}

	@Override
	public abstract String toString();
	
	public abstract ModPage getPage() throws CannotAddModException;
	public abstract boolean isUpdateAvailable();
}
