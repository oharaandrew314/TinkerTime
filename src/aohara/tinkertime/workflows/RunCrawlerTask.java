package aohara.tinkertime.workflows;

import java.io.IOException;

import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.crawlers.Crawler;

class RunCrawlerTask extends WorkflowTask {
	
	private final Crawler<?> crawler;

	RunCrawlerTask(Crawler<?> crawler) {
		super(String.format("Crawling %s page", crawler.pageUrl.getHost()));
		this.crawler = crawler;
	}

	@Override
	public boolean execute() throws Exception {
		crawler.call();
		return true;
	}

	@Override
	protected int findTargetProgress() throws IOException {
		return -1;
	}
}
