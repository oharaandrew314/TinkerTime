package aohara.tinkertime.controllers.fileUpdater;

import java.io.IOException;
import java.nio.file.Path;

import aohara.common.workflows.TaskListener;
import aohara.common.workflows.Workflow;
import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.controllers.WorkflowRunner;
import aohara.tinkertime.controllers.crawlers.Crawler;
import aohara.tinkertime.workflows.DownloadFileWorkflow;

/**
 * Download Action to download ModuleManager for the {@link aohara.tinkertime.views.FileUpdateDialog}.
 * 
 * Launches the Download with a {@link aohara.common.workflows.Workflow}
 * and notifies the dialog when the update is complete.
 * 
 * @author Andrew O'Hara
 */
@SuppressWarnings("serial")
public class ModuleManagerDownloader extends FileDownloadController implements TaskListener {
	
	private final WorkflowRunner runner;
	private final Path destFolderPath;
	private final CurrentVersion currentVersion;
	
	public ModuleManagerDownloader(WorkflowRunner runner, Crawler<?, ?> crawler, Path destFolderPath, CurrentVersion currentVersion){
		super(crawler);
		if(!destFolderPath.toFile().isDirectory()){
			throw new IllegalArgumentException("Destination path must be a folder");
		}
		
		this.runner = runner;
		this.destFolderPath = destFolderPath;
		this.currentVersion = currentVersion;
	}

	@Override
	public void download(Crawler<?, ?> crawler) throws IOException {		
		Workflow workflow = new DownloadFileWorkflow("Updating Module Manager", crawler, destFolderPath);
		if (currentVersion.exists()){
			workflow.queueDelete(currentVersion.getPath());
		}
		workflow.addListener(this);
		runner.submitDownloadWorkflow(workflow);
	}
	
	@Override
	public void taskComplete(WorkflowTask task, boolean tasksRemaining) {
		getDialog().updateCurrentVersion();
	}
	
	// -- Unused -----------------------------------------------------------
	
	@Override
	public void taskStarted(WorkflowTask task, int targetProgress) {
		// No Action
	}

	@Override
	public void taskProgress(WorkflowTask task, int increment) {
		// No Action
	}

	@Override
	public void taskError(WorkflowTask task, boolean tasksRemaining, Exception e) {
		// No Action
	}
}
