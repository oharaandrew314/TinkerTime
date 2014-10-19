package aohara.tinkertime.workflows.tasks;

import java.io.IOException;
import java.util.Date;

import aohara.common.workflows.Workflow.WorkflowTask;
import aohara.tinkertime.crawlers.Crawler;

/**
 * Workflow Task that returns true if an update for a file is available.
 * 
 * @author Andrew O'Hara
 */
public class CheckForUpdateTask extends WorkflowTask {
	
	private final Date lastUpdated;
	private final String lastFileName;
	private final Crawler<?> crawler;

	public CheckForUpdateTask(Crawler<?> crawler, Date lastUpdated, String lastFileName) {
		this.crawler = crawler;
		this.lastUpdated = lastUpdated;
		this.lastFileName = lastFileName;
	}

	@Override
	public Boolean call() throws Exception {
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
