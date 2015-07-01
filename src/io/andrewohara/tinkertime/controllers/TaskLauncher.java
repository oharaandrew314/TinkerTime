package io.andrewohara.tinkertime.controllers;

import io.andrewohara.common.workflows.tasks.WorkflowBuilder;
import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateCoordinatorImpl;
import io.andrewohara.tinkertime.db.ConfigFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import com.google.inject.Inject;

public class TaskLauncher {

	private final ConfigFactory configFactory;
	private final ThreadPoolExecutor downloadExecutor;
	private final Executor fileExecutor;
	private final ModUpdateCoordinatorImpl modUpdateCoordinator;

	@Inject
	TaskLauncher(ConfigFactory configFactory, ThreadPoolExecutor downloadExecutor, Executor fileExecutor, ModUpdateCoordinatorImpl modUpdateCoordinator){
		this.configFactory = configFactory;
		this.downloadExecutor = downloadExecutor;
		this.fileExecutor = fileExecutor;
		this.modUpdateCoordinator = modUpdateCoordinator;
	}

	public void submitDownloadWorkflow(WorkflowBuilder builder){
		builder.addListener(modUpdateCoordinator);

		// Reset thread pool size if size in options has changed
		int numDownloadThreads = configFactory.getConfig().getNumConcurrentDownloads();
		if (downloadExecutor.getMaximumPoolSize() != numDownloadThreads){
			downloadExecutor.setCorePoolSize(numDownloadThreads);
			downloadExecutor.setMaximumPoolSize(numDownloadThreads);
		}

		builder.execute(downloadExecutor);
	}

	public void submitFileWorkflow(WorkflowBuilder builder){
		builder.addListener(modUpdateCoordinator);
		builder.execute(fileExecutor);
	}
}
