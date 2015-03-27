package aohara.tinkertime.workflows;

import java.io.IOException;

import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.models.FileUpdateListener;

/**
 * Workflow Task that notifies the given UpdateListeners that an update is available
 * for the file represented by the given Crawler.
 * 
 * @author Andrew O'Hara
 */
//TODO: remove in favor of event model
class NotfiyUpdateAvailableTask extends WorkflowTask {
	
	private final FileUpdateListener[] listeners;
	private final Crawler<?> crawler;

	NotfiyUpdateAvailableTask(Crawler<?> crawler, FileUpdateListener... listeners) {
		super("Registering Update Available");
		this.listeners = listeners;
		this.crawler = crawler;
	}

	@Override
	public boolean execute() throws Exception {	
		// Notify update listeners
		if (crawler.isAssetsAvailable()){
			for (FileUpdateListener l : listeners){
				l.setUpdateAvailable(crawler);
				progress(1);
			}
			return true;
		}
		return false;
	}

	@Override
	protected int findTargetProgress() throws IOException {
		return listeners.length;
	}

}
