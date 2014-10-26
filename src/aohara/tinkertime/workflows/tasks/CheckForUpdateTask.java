package aohara.tinkertime.workflows.tasks;

import java.io.IOException;
import java.util.Date;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.Workflow.WorkflowTask;
import aohara.tinkertime.workflows.DownloaderContext;

/**
 * Workflow Task that returns true if an update for a file is available.
 * 
 * @author Andrew O'Hara
 */
public class CheckForUpdateTask extends WorkflowTask {
	
	private final Date lastUpdated;
	private final String lastFileName;
	private final DownloaderContext context;

	public CheckForUpdateTask(DownloaderContext context, Date lastUpdated, String lastFileName) {
		this.context = context;
		this.lastUpdated = lastUpdated;
		this.lastFileName = lastFileName;
	}

	@Override
	public boolean call(Workflow workflow) throws Exception {
		return context.isUpdateAvailable(lastUpdated, lastFileName);
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
