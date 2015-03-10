package aohara.tinkertime.workflows.tasks;

import java.io.IOException;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.Workflow.WorkflowTask;
import aohara.tinkertime.crawlers.VersionInfo;
import aohara.tinkertime.workflows.DownloaderContext;

/**
 * Workflow Task that returns true if an update for a file is available.
 * 
 * @author Andrew O'Hara
 */
public class CheckForUpdateTask extends WorkflowTask {
	
	private final DownloaderContext context;
	private final VersionInfo currentVersion;

	public CheckForUpdateTask(DownloaderContext context, VersionInfo currentVersion) {
		this.context = context;
		this.currentVersion = currentVersion;
	}

	@Override
	public boolean call(Workflow workflow) throws Exception {
		return context.crawler.isUpdateAvailable(currentVersion);
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
