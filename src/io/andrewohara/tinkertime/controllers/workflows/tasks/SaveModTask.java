package io.andrewohara.tinkertime.controllers.workflows.tasks;

import io.andrewohara.common.workflows.tasks.WorkflowTask;
import io.andrewohara.tinkertime.io.crawlers.Crawler;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.io.IOException;
import java.sql.SQLException;

public class SaveModTask extends WorkflowTask {

	private final Crawler<?> crawler;
	private final Mod mod;

	public SaveModTask(Crawler<?> crawler, Mod mod) {
		super("Saving Mod");
		this.crawler = crawler;
		this.mod = mod;
	}

	@Override
	public boolean execute() throws IOException, SQLException {
		mod.update(crawler.getModUpdateData());
		mod.setUpdateAvailable(false);
		mod.commit();
		return true;
	}

	@Override
	protected int findTargetProgress() throws IOException {
		return 0;
	}
}
