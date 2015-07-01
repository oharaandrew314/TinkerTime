package io.andrewohara.tinkertime.controllers.workflows.tasks;

import io.andrewohara.common.workflows.tasks.WorkflowTask;
import io.andrewohara.tinkertime.io.crawlers.Crawler;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.io.IOException;

/**
 * Workflow Task that returns true if an update for a file is available.
 *
 * @author Andrew O'Hara
 */
public class CheckForUpdateTask extends WorkflowTask {

	private final Crawler<?> crawler;

	public CheckForUpdateTask(Crawler<?> crawler) {
		super("Comparing Versions");
		this.crawler = crawler;
	}

	@Override
	public boolean execute() throws IOException {
		Mod mod = crawler.getMod();
		try{
			return crawler.getVersion().greaterThan(mod.getModVersion());
		} catch (NullPointerException e){
			try {
				return crawler.getUpdatedOn().before(mod.getUpdatedOn());
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
