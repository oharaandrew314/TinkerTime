package aohara.tinkertime.workflows.tasks;

import java.io.IOException;

import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.crawlers.Crawler;

public class RunCrawlerTask extends WorkflowTask {
	
	private final Crawler<?> crawler;

	public RunCrawlerTask(Crawler<?> crawler) {
		super(String.format("Crawling %s page", crawler.pageUrl.getHost()));
		this.crawler = crawler;
	}

	@Override
	public boolean execute() throws IOException {
		crawler.testConnection();
		return true;
	}

	@Override
	protected int findTargetProgress() throws IOException {
		return -1;
	}
}
