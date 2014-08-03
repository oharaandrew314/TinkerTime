package aohara.tinkertime.workflows.tasks;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Path;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.tasks.FileTransferTask;
import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.Config;
import aohara.tinkertime.controllers.ModManager.CannotAddModException;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.pages.ModPage;
import aohara.tinkertime.models.pages.PageFactory;

public class DownloadModFromPageTask extends WorkflowTask {
	
	private final Path pagePath, dest;
	private final URL pageUrl;
	private ModPage cachedPage;
	private final ModStateManager sm;

	public DownloadModFromPageTask(Workflow workflow, Config config, Path pagePath, URL pageUrl, ModStateManager sm) {
		super(workflow);
		this.pagePath = pagePath;
		this.pageUrl = pageUrl;
		this.sm = sm;
		dest = config.getModsPath();
	}

	@Override
	public Boolean call() throws Exception {
		URL downloadLink = getPage().getDownloadLink();
		try (
			InputStream is = new BufferedInputStream(downloadLink.openStream());
			OutputStream os = new FileOutputStream(FileTransferTask.groomDestinationPath(downloadLink, dest).toFile());
		){
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = is.read(buf)) > 0) {
				os.write(buf, 0, bytesRead);
				progress(bytesRead);
			}
		}
		sm.modUpdated(new Mod(getPage()), false);
		return true;
	}
	
	private ModPage getPage() throws CannotAddModException{
		if (cachedPage == null){
			cachedPage = PageFactory.loadModPage(pagePath, pageUrl);
		}
		return cachedPage;
	}

	@Override
	public int getTargetProgress() throws InvalidContentException {
		try {
			return getPage().getDownloadLink().openConnection().getContentLength();
		} catch (IOException | CannotAddModException e) {
			throw new InvalidContentException();
		}
	}
}
