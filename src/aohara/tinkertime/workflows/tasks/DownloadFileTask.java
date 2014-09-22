package aohara.tinkertime.workflows.tasks;

import java.io.IOException;
import java.nio.file.Path;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.tasks.FileTransferTask;
import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.controllers.crawlers.Crawler;

/**
 * Workflow Task that Download's the file discovered by the given crawler.
 * 
 * @author Andrew O'Hara
 */
public class DownloadFileTask extends WorkflowTask {
	
	private final Crawler<?> crawler;
	private final Path destFolder;

	public DownloadFileTask(Workflow workflow, Crawler<?> crawler, Path destFolder) {
		super(workflow);
		if (!destFolder.toFile().isDirectory()){
			throw new IllegalArgumentException("Destination must be a folder");
		}
		
		this.crawler = crawler;
		this.destFolder = destFolder;
	}

	@Override
	public Boolean call() throws Exception {
		FileTransferTask.transferFile(
			this,
			crawler.getDownloadLink(),
			destFolder.resolve(crawler.getNewestFileName())
		);
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
