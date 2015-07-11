package io.andrewohara.tinkertime.controllers.workflows;

import io.andrewohara.common.workflows.tasks.WorkflowBuilder;
import io.andrewohara.tinkertime.controllers.ModUpdateCoordinator;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import com.google.inject.Inject;

public class TaskLauncher {

	private final ThreadPoolExecutor downloadExecutor;
	private final Executor fileExecutor;
	private final ModUpdateCoordinator modUpdateCoordinator;

	@Inject
	TaskLauncher(ThreadPoolExecutor downloadExecutor, Executor fileExecutor, ModUpdateCoordinator modUpdateCoordinator){
		this.downloadExecutor = downloadExecutor;
		this.fileExecutor = fileExecutor;
		this.modUpdateCoordinator = modUpdateCoordinator;
	}

	public void submitDownloadWorkflow(WorkflowBuilder builder){
		builder.addListener(modUpdateCoordinator);
		builder.execute(downloadExecutor);
	}

	public void submitFileWorkflow(WorkflowBuilder builder){
		builder.addListener(modUpdateCoordinator);
		builder.execute(fileExecutor);
	}
}
