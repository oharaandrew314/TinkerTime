package aohara.tinkertime.workflows;

import java.io.IOException;
import java.net.MalformedURLException;

import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.workflows.contexts.DownloaderContext;

class CacheCrawlerPageTask extends WorkflowTask {
	
	private final DownloaderContext context;

	CacheCrawlerPageTask(DownloaderContext context) {
		super(getTitle(context));
		this.context = context;
	}
	
	private static String getTitle(DownloaderContext context) {
		String pageName = "Unknown Page";
		try {
			pageName = context.crawler.getApiUrl().getHost();
		} catch (MalformedURLException e) {
			// Do Nothing
		}
		return String.format("Crawling %s page", pageName);
	}

	@Override
	public boolean execute() throws Exception {
		Crawler<?> crawler = context.crawler;
		crawler.getPage(crawler.getApiUrl());
		return true;
	}

	@Override
	protected int findTargetProgress() throws IOException {
		return context.crawler.getApiUrl().openConnection().getContentLength();
	}
}
