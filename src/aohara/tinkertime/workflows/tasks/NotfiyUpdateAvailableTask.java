package aohara.tinkertime.workflows.tasks;

import java.io.IOException;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.controllers.crawlers.Crawler;
import aohara.tinkertime.models.UpdateListener;

public class NotfiyUpdateAvailableTask extends WorkflowTask {
	
	private final UpdateListener[] listeners;
	private final Crawler<?, ?> crawler;

	public NotfiyUpdateAvailableTask(Workflow workflow, Crawler<?, ?> crawler, UpdateListener... listeners) {
		super(workflow);
		this.listeners = listeners;
		this.crawler = crawler;
	}

	@Override
	public Boolean call() throws Exception {		
		// Notify update listeners
		String newestFileName = crawler.getNewestFileName();
		if (newestFileName != null){
			for (UpdateListener l : listeners){
				l.setUpdateAvailable(crawler.url, newestFileName);
				progress(1);
			}
			return true;
		}
		return false;
	}

	@Override
	public int getTargetProgress() throws IOException {
		return listeners.length;
	}

}
