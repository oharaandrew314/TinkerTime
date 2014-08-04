package aohara.tinkertime.workflows.tasks;

import java.net.URL;
import java.nio.file.Path;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.models.UpdateListener;
import aohara.tinkertime.models.pages.FilePage;
import aohara.tinkertime.models.pages.PageFactory;

public class NotfiyUpdateAvailableTask extends WorkflowTask {
	
	private final UpdateListener[] listeners;
	private final Path pagePath;
	private final URL pageUrl;

	public NotfiyUpdateAvailableTask(Workflow workflow, Path pagePath, URL pageUrl, UpdateListener... listeners) {
		super(workflow);
		this.listeners = listeners;
		this.pagePath = pagePath;
		this.pageUrl = pageUrl;
	}

	@Override
	public Boolean call() throws Exception {
		FilePage page = PageFactory.loadFilePage(pagePath, pageUrl);
		
		// Notify update listeners
		if (page != null){
			for (UpdateListener l : listeners){
				l.setUpdateAvailable(page);
				progress(1);
			}
			return true;
		}
		return false;
	}

	@Override
	public int getTargetProgress() throws InvalidContentException {
		return listeners.length;
	}

}
