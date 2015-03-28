package aohara.tinkertime.workflows;

import java.io.IOException;
import java.util.Date;

import com.github.zafarkhaja.semver.Version;

import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.crawlers.Crawler;

/**
 * Workflow Task that returns true if an update for a file is available.
 * 
 * @author Andrew O'Hara
 */
class CheckForUpdateTask extends WorkflowTask {
	
	private final Crawler<?> crawler;
	private final Version currentVersion;
	private final Date lastUpdatedOn;

	CheckForUpdateTask(Crawler<?> crawler, Version currentVersion, Date lastUpdatedOn) {
		super("Comparing versions");
		this.crawler = crawler;
		this.currentVersion = currentVersion;
		this.lastUpdatedOn = lastUpdatedOn;
	}

	@Override
	public boolean execute() throws Exception {
		if (crawler.isUpdateAvailable(currentVersion, lastUpdatedOn)){
			setResult(crawler);
			return true;
		}
		return false;
	}
	
	@Override
	protected int findTargetProgress() throws IOException {
		return -1;
	}
}
