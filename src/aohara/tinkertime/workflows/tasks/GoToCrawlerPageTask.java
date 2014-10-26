package aohara.tinkertime.workflows.tasks;

import java.io.IOException;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.Workflow.WorkflowTask;
import aohara.common.workflows.tasks.BrowserGoToTask;
import aohara.tinkertime.crawlers.Crawler;

public class GoToCrawlerPageTask extends WorkflowTask {
	
	private final Crawler<?> crawler;
	
	public GoToCrawlerPageTask(Crawler<?> crawler) {
		this.crawler = crawler;
	}

	@Override
	public int getTargetProgress() throws IOException {
		return -1;
	}

	@Override
	public String getTitle() {
		return "Going to Crawler Page";
	}

	@Override
	public boolean call(Workflow workflow) throws Exception {
		return new BrowserGoToTask(crawler.getPageUrl()).call(workflow);
	}
	
	

}
