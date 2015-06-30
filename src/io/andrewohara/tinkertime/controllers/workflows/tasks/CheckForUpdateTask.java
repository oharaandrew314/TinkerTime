package io.andrewohara.tinkertime.controllers.workflows.tasks;

import io.andrewohara.common.version.Version;
import io.andrewohara.common.workflows.tasks.WorkflowTask;
import io.andrewohara.tinkertime.io.crawlers.Crawler;

import java.io.IOException;
import java.util.Date;

/**
 * Workflow Task that returns true if an update for a file is available.
 * 
 * @author Andrew O'Hara
 */
public class CheckForUpdateTask extends WorkflowTask {
	
	private final Crawler<?> crawler;
	private final Version currentVersion;
	private final Date lastUpdatedOn;

	public CheckForUpdateTask(Crawler<?> crawler, Version currentVersion, Date lastUpdatedOn) {
		super("Comparing Versions");
		this.crawler = crawler;
		this.currentVersion = currentVersion;
		this.lastUpdatedOn = lastUpdatedOn;
	}

	@Override
	public boolean execute() throws IOException {
		try{
			return crawler.getVersion().greaterThan(currentVersion);
		} catch (NullPointerException e){
			try {
				return crawler.getUpdatedOn().before(lastUpdatedOn);
			} catch (NullPointerException | IOException e1) {
				return false;
			}
		}
	}
	
	@Override
	protected int findTargetProgress() throws IOException {
		return -1;
	}
}
