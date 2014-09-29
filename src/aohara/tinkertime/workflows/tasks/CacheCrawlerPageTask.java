package aohara.tinkertime.workflows.tasks;

import java.io.IOException;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.crawlers.Crawler;

public class CacheCrawlerPageTask extends WorkflowTask {
	
	private final Crawler<?> crawler;

	public CacheCrawlerPageTask(Workflow workflow, Crawler<?> crawler) {
		super(workflow);
		this.crawler = crawler;
	}

	@Override
	public Boolean call() throws Exception {
		crawler.getPage(crawler.url);
		return true;
	}

	@Override
	public int getTargetProgress() throws IOException {
		return crawler.url.openConnection().getContentLength();
	}

	@Override
	public String getTitle() {
		return String.format("Crawling %s page", crawler.url.getHost());
	}
}
