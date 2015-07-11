package io.andrewohara.tinkertime.controllers.workflows.tasks;

import io.andrewohara.common.workflows.tasks.WorkflowTask;
import io.andrewohara.tinkertime.io.crawlers.Crawler;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Workflow Task that returns true if an update for a file is available.
 *
 * @author Andrew O'Hara
 */
public class CheckForUpdateTask extends WorkflowTask {

	private final Crawler<?> crawler;
	private final Mod mod;
	private final boolean markIfAvailable;

	public CheckForUpdateTask(Crawler<?> crawler, Mod mod, boolean markIfAvailable) {
		super("Comparing Versions");
		this.crawler = crawler;
		this.mod = mod;
		this.markIfAvailable = markIfAvailable;
	}

	@Override
	public boolean execute() throws IOException, SQLException {
		boolean updateAvailable = crawler.isUpdateAvailable(mod);
		if (markIfAvailable && updateAvailable){
			mod.setUpdateAvailable(updateAvailable);
			mod.commit();
		}

		return updateAvailable;
	}

	@Override
	protected int findTargetProgress() throws IOException {
		return -1;
	}
}
