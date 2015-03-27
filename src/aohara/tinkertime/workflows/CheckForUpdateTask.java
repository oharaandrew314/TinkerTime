package aohara.tinkertime.workflows;

import java.io.IOException;

import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.VersionInfo;

/**
 * Workflow Task that returns true if an update for a file is available.
 * 
 * @author Andrew O'Hara
 */
class CheckForUpdateTask extends WorkflowTask {
	
	private final Crawler<?> crawler;
	private final VersionInfo currentVersion;

	CheckForUpdateTask(Crawler<?> crawler, VersionInfo currentVersion) {
		super("Comparing versions");
		this.crawler = crawler;
		this.currentVersion = currentVersion;
	}

	@Override
	public boolean execute() throws Exception {
		if (crawler.isUpdateAvailable(currentVersion)){
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
