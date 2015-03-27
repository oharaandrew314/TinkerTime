package aohara.tinkertime.workflows;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import aohara.common.workflows.tasks.FileTransferTask;
import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.workflows.ModWorkflowBuilder.ModDownloadType;

class CrawlerDownloadTask extends WorkflowTask {
	
	private final Crawler<?> crawler;
	private final ModDownloadType type;
	private final Path dest;
	
	CrawlerDownloadTask(Crawler<?> crawler, ModDownloadType type, Path dest){
		super("Downloading assets");
		this.crawler = crawler;
		this.type = type;
		this.dest = dest;
	}
	
	private URL getUrl() throws IOException{
		switch(type){
		case File: return crawler.getDownloadLink();
		case Image: return crawler.getImageUrl();
		default: return null;
		}
	}

	@Override
	public boolean execute() throws Exception {	
		return new FileTransferTask(getUrl() != null ? getUrl().toURI() : null, dest).call(getWorkflow());
	}

	@Override
	protected int findTargetProgress() throws IOException {
		URL url = getUrl();
		if (url != null){
			return url.openConnection().getContentLength();
		}
		return -1;
	}
}
