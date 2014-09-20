package aohara.tinkertime.workflows;

import java.io.IOException;
import java.nio.file.Path;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.controllers.crawlers.Crawler;
import aohara.tinkertime.workflows.tasks.DownloadFileTask;

/**
 * Workflow that will Download the File that the given Crawler will discover.
 * 
 * @author Andrew O'Hara
 */
public class CrawlerDownloadFileWorkflow extends Workflow{

	public CrawlerDownloadFileWorkflow(String name, Crawler<?> crawler, Path destPath) {
		super(name);
		
		addTask(new CachePageTask(this, crawler));		
		addTask(new DownloadFileTask(this, crawler, destPath));
	}
	
	private class CachePageTask extends WorkflowTask {
		
		private final Crawler<?> crawler;

		public CachePageTask(Workflow workflow, Crawler<?> crawler) {
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

}
