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
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.controllers.crawlers.Crawler;
import aohara.tinkertime.models.Mod;

public class DownloadModTask extends WorkflowTask {
	// TODO Instead use DownloadFileTask and then create MarkModUpdated task
	
	private final Path dest;
	private final ModStateManager sm;
	private final Crawler<Mod, ?> crawler;

	public DownloadModTask(Workflow workflow, Crawler<Mod, ?> crawler, Config config, ModStateManager sm) {
		super(workflow);
		this.sm = sm;
		this.crawler = crawler;
		dest = config.getModsPath();
	}

	@Override
	public Boolean call() throws Exception {
		URL downloadLink = crawler.getDownloadLink();
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
		sm.modUpdated(crawler.crawl(), false);
		return true;
	}

	@Override
	public int getTargetProgress() throws IOException {
		return crawler.getDownloadLink().openConnection().getContentLength();
	}
}
