package aohara.tinkertime.workflows.tasks;

import java.nio.file.Path;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.pages.ModPage;
import aohara.tinkertime.models.pages.PageFactory;

public class MarkModUpdatedTask extends WorkflowTask {

	private final Mod existingMod;
	private final Path downloadedPagePath;
	private final ModStateManager sm;
	
	public MarkModUpdatedTask(Workflow workflow, Mod existingMod, Path downloadedPagePath, ModStateManager sm) {
		super(workflow);
		this.existingMod = existingMod;
		this.downloadedPagePath = downloadedPagePath;
		this.sm = sm;
	}

	@Override
	public Boolean call() throws Exception {
		ModPage page = PageFactory.loadModPage(downloadedPagePath, existingMod.getPageUrl());
		if (page.isUpdateAvailable(existingMod.getUpdatedOn())){
			existingMod.setUpdateAvailable();
			sm.modUpdated(existingMod, false);
		}
		return true;
	}

	@Override
	public int getTargetProgress() throws InvalidContentException {
		return 1;
	}

}
