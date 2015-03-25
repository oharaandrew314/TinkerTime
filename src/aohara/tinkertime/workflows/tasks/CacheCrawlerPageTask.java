package aohara.tinkertime.workflows.tasks;

import java.io.IOException;
import java.net.MalformedURLException;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.Workflow.WorkflowTask;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.workflows.contexts.DownloaderContext;

public class CacheCrawlerPageTask extends WorkflowTask {
	
	private final DownloaderContext context;

	public CacheCrawlerPageTask(DownloaderContext context) {
		this.context = context;
	}

	@Override
	public boolean call(Workflow workflow) throws Exception {
		Crawler<?> crawler = context.crawler;
		crawler.getPage(crawler.getApiUrl());
		return true;
	}

	@Override
	public int getTargetProgress() throws IOException {
		return context.crawler.getApiUrl().openConnection().getContentLength();
	}

	@Override
	public String getTitle() {
		String pageName = "Unknown Page";
		try {
			pageName = context.crawler.getApiUrl().getHost();
		} catch (MalformedURLException e) {
			// Do Nothing
		}
		return String.format("Crawling %s page", pageName);
	}
}
