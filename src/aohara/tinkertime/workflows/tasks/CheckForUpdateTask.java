package aohara.tinkertime.workflows.tasks;

import java.nio.file.Path;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.models.DownloadedFile;
import aohara.tinkertime.models.pages.ModPage;
import aohara.tinkertime.models.pages.PageFactory;

public class CheckForUpdateTask extends WorkflowTask {
	
	private final Path newPagePath;
	private final DownloadedFile existing;

	public CheckForUpdateTask(Workflow workflow, Path newPagePath, DownloadedFile existing) {
		super(workflow);
		this.newPagePath = newPagePath;
		this.existing = existing;
	}

	@Override
	public Boolean call() throws Exception {
		ModPage page = PageFactory.loadModPage(newPagePath, existing.getPageUrl());
		return page.isUpdateAvailable(existing.getUpdatedOn());
	}

	@Override
	public int getTargetProgress() throws InvalidContentException {
		// TODO Auto-generated method stub
		return 0;
	}

}
