package aohara.tinkertime.workflows.tasks;

import java.net.URL;
import java.nio.file.Path;
import java.util.Date;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.models.pages.FilePage;
import aohara.tinkertime.models.pages.PageFactory;

public class CheckForUpdateTask extends WorkflowTask {
	
	private final Path newPagePath;
	private final Date lastUpdated;
	private final String lastFileName;
	private final URL pageUrl;

	public CheckForUpdateTask(
			Workflow workflow, Path newPagePath, URL pageUrl, Date lastUpdated,
			String lastFileName) {
		super(workflow);
		this.newPagePath = newPagePath;
		this.lastUpdated = lastUpdated;
		this.lastFileName = lastFileName;
		this.pageUrl = pageUrl;
	}

	@Override
	public Boolean call() throws Exception {
		FilePage page = PageFactory.loadFilePage(newPagePath, pageUrl);
		
		// Check if update is available
		if (lastUpdated != null && page != null){
			return page.isUpdateAvailable(lastUpdated);
		} else if (page != null && lastFileName != null){
			return !page.getNewestFileName().equals(lastFileName);
		}
		return page != null;
	}

	@Override
	public int getTargetProgress() throws InvalidContentException {
		return 1;
	}
}
