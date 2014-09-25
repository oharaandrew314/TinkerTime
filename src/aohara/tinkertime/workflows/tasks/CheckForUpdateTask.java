package aohara.tinkertime.workflows.tasks;

import java.io.IOException;
import java.util.Date;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.controllers.crawlers.Crawler;

/**
 * Workflow Task that returns true if an update for a file is available.
 * 
 * @author Andrew O'Hara
 */
public class CheckForUpdateTask extends WorkflowTask {
	
	private final Date lastUpdated;
	private final String lastFileName;
	private final Crawler<?> crawler;

	public CheckForUpdateTask(Workflow workflow, Crawler<?> crawler, Date lastUpdated, String lastFileName) {
		super(workflow);
		this.crawler = crawler;
		this.lastUpdated = lastUpdated;
		this.lastFileName = lastFileName;
	}

	@Override
	public Boolean call() throws Exception {
		System.out.println(crawler.getNewestFileName() + " " + crawler.isUpdateAvailable(lastUpdated, lastFileName));
		return crawler.isUpdateAvailable(lastUpdated, lastFileName);
	}

	@Override
	public int getTargetProgress() throws IOException {
		return -1;
	}

	@Override
	public String getTitle() {
		return "Comparing versions";
	}
}
