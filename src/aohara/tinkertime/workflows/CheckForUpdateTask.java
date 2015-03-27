package aohara.tinkertime.workflows;

import java.io.IOException;

import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.crawlers.VersionInfo;
import aohara.tinkertime.workflows.contexts.DownloaderContext;

/**
 * Workflow Task that returns true if an update for a file is available.
 * 
 * @author Andrew O'Hara
 */
class CheckForUpdateTask extends WorkflowTask {
	
	private final DownloaderContext context;
	private final VersionInfo currentVersion;

	CheckForUpdateTask(DownloaderContext context, VersionInfo currentVersion) {
		super("Comparing versions");
		this.context = context;
		this.currentVersion = currentVersion;
	}

	@Override
	public boolean execute() throws Exception {
		return context.crawler.isUpdateAvailable(currentVersion);
	}
	
	@Override
	protected int findTargetProgress() throws IOException {
		return -1;
	}
}
