package aohara.tinkertime.workflows;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.UpdateCheckCrawler;

import com.github.zafarkhaja.semver.Version;

/**
 * Workflow Task that returns true if an update for a file is available.
 * 
 * @author Andrew O'Hara
 */
public class CheckForUpdateTask extends WorkflowTask {
	
	private final UpdateCheckCrawler crawler;
	private final OnUpdateAvailable onUpdateAvailable;

	CheckForUpdateTask(Crawler<?> crawler, Version currentVersion, Date lastUpdatedOn, OnUpdateAvailable onUpdateAvailable) {
		this(new UpdateCheckCrawler(crawler, currentVersion, lastUpdatedOn), onUpdateAvailable);
	}
	
	CheckForUpdateTask(UpdateCheckCrawler updateCheckCrawler, OnUpdateAvailable onUpdateAvailable){
		super("Comparing Versions");
		this.crawler = updateCheckCrawler;
		this.onUpdateAvailable = onUpdateAvailable;
	}

	@Override
	public boolean execute() throws IOException {
		if (crawler.isUpdateAvailable()){
			if (onUpdateAvailable != null){
				onUpdateAvailable.onUpdateAvailable(crawler.getVersion(), crawler.getDownloadLink());
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected int findTargetProgress() throws IOException {
		return -1;
	}
	
	public interface OnUpdateAvailable {
		
		void onUpdateAvailable(Version newVersion, URL downloadLink);
		
	}
}
