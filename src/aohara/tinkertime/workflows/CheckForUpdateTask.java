package aohara.tinkertime.workflows;

import java.io.IOException;
import java.util.Date;

import aohara.common.version.Version;
import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.UpdateCheckCrawler;

/**
 * Workflow Task that returns true if an update for a file is available.
 * 
 * @author Andrew O'Hara
 */
public class CheckForUpdateTask extends WorkflowTask {
	
	private final UpdateCheckCrawler crawler;

	CheckForUpdateTask(Crawler<?> crawler, Version currentVersion, Date lastUpdatedOn) {
		this(new UpdateCheckCrawler(crawler, currentVersion, lastUpdatedOn));
	}
	
	CheckForUpdateTask(UpdateCheckCrawler updateCheckCrawler){
		super("Comparing Versions");
		this.crawler = updateCheckCrawler;
	}

	@Override
	public boolean execute() throws IOException {
		return crawler.isUpdateAvailable();
	}
	
	@Override
	protected int findTargetProgress() throws IOException {
		return -1;
	}
}
