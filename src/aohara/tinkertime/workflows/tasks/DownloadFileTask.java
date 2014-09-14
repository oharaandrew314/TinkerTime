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
import aohara.tinkertime.controllers.crawlers.Crawler;

public class DownloadFileTask extends WorkflowTask {
	
	private final Crawler<?, ?> crawler;
	private final Path destFolder;

	public DownloadFileTask(Workflow workflow, Crawler<?, ?> crawler, Path destFolder) {
		super(workflow);
		if (!destFolder.toFile().isDirectory()){
			throw new IllegalArgumentException("Destination must be a folder");
		}
		
		this.crawler = crawler;
		this.destFolder = destFolder;
	}

	@Override
	public Boolean call() throws Exception {
		URL downloadLink = crawler.getDownloadLink();
		Path dest = destFolder.resolve(crawler.getNewestFileName());
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
		return true;
	}

	@Override
	public int getTargetProgress() throws IOException {
		return crawler.getDownloadLink().openConnection().getContentLength();
	}

	@Override
	public String getTitle() {
		return String.format("Downloading %s", crawler.url.getFile());
	}

}
