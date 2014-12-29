package aohara.tinkertime.workflows.tasks;

import java.io.IOException;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.Workflow.WorkflowTask;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.models.FileUpdateListener;

/**
 * Workflow Task that notifies the given UpdateListeners that an update is available
 * for the file represented by the given Crawler.
 * 
 * @author Andrew O'Hara
 */
public class NotfiyUpdateAvailableTask extends WorkflowTask {
	
	private final FileUpdateListener[] listeners;
	private final Crawler<?> crawler;

	public NotfiyUpdateAvailableTask(Crawler<?> crawler, FileUpdateListener... listeners) {
		this.listeners = listeners;
		this.crawler = crawler;
	}

	@Override
	public boolean call(Workflow workflow) throws Exception {		
		// Notify update listeners
		if (crawler.isAssetsAvailable()){
			for (FileUpdateListener l : listeners){
				l.setUpdateAvailable(crawler);
				progress(workflow, 1);
			}
			return true;
		}
		return false;
	}

	@Override
	public int getTargetProgress() throws IOException {
		return listeners.length;
	}

	@Override
	public String getTitle() {
		return "Registering Update Available";
	}

}
