package io.andrewohara.tinkertime.controllers.workflows.tasks;

import io.andrewohara.common.workflows.tasks.WorkflowTask;
import io.andrewohara.tinkertime.io.crawlers.Crawler;

import java.io.IOException;

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
