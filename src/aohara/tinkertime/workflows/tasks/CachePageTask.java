package aohara.tinkertime.workflows.tasks;

import java.io.IOException;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.controllers.crawlers.Crawler;

/**
 * Requests that a Crawler download its main page.
 * 
 * This Task does not guarantee that the Crawler has actually cached it.
 * It is up to the Crawler's implementation to cache the page.
 * 
 * This workflow is useful for accounting for more of the time spent crawling and
 * reporting it to the ProgressListener.
 * 
 * TODO: This isn't all that necessary.  Downloading and crawling the page can
 * just be followed using the same task and report an indeterminate progress. 
 * 
 * @author Andrew O'Hara
 */
public class CachePageTask extends WorkflowTask {
	
	private final Crawler<?, ?> crawler;

	public CachePageTask(Workflow workflow, Crawler<?, ?> crawler) {
		super(workflow);
		this.crawler = crawler;
	}

	@Override
	public Boolean call() throws Exception {
		crawler.crawl();
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
