package aohara.tinkertime.workflows;

import java.io.IOException;
import java.net.MalformedURLException;

import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.crawlers.Crawler;

class CacheCrawlerPageTask extends WorkflowTask {
	
	private final Crawler<?> crawler;

	CacheCrawlerPageTask(Crawler<?> crawler) {
		super(getTitle(crawler));
		this.crawler = crawler;
	}
	
	private static String getTitle(Crawler<?> crawler) {
		String pageName = "Unknown Page";
		try {
			pageName = crawler.getApiUrl().getHost();
		} catch (MalformedURLException e) {
			// Do Nothing
		}
		return String.format("Crawling %s page", pageName);
	}

	@Override
	public boolean execute() throws Exception {
		crawler.getPage(crawler.getApiUrl());
		return true;
	}

	@Override
	protected int findTargetProgress() throws IOException {
		return crawler.getApiUrl().openConnection().getContentLength();
	}
}
