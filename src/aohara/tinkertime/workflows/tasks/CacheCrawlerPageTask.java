package aohara.tinkertime.workflows.tasks;

import java.io.IOException;

import aohara.common.workflows.Workflow.WorkflowTask;
import aohara.tinkertime.crawlers.Crawler;

public class CacheCrawlerPageTask extends WorkflowTask {
	
	private final Crawler<?> crawler;

	public CacheCrawlerPageTask(Crawler<?> crawler) {
		this.crawler = crawler;
	}

	@Override
	public Boolean call() throws Exception {
		crawler.getPage(crawler.getApiUrl());
		return true;
	}

	@Override
	public int getTargetProgress() throws IOException {
		return crawler.getApiUrl().openConnection().getContentLength();
	}

	@Override
	public String getTitle() {
		return String.format("Crawling %s page", crawler.getApiUrl().getHost());
	}
}
