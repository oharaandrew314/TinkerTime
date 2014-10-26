package aohara.tinkertime.workflows.tasks;

import java.io.IOException;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.Workflow.WorkflowTask;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.workflows.DownloaderContext;

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
		return String.format("Crawling %s page", context.crawler.getApiUrl().getHost());
	}
}
