package io.andrewohara.tinkertime.controllers.workflows.tasks;

import io.andrewohara.common.workflows.tasks.BrowserGoToTask;
import io.andrewohara.common.workflows.tasks.WorkflowTask;
import io.andrewohara.tinkertime.io.crawlers.Crawler;
import io.andrewohara.tinkertime.models.mod.Mod;
import io.andrewohara.tinkertime.models.mod.ModUpdateData;

import java.io.IOException;

import javax.swing.JOptionPane;

public class DownloadModInBrowserTask extends WorkflowTask {

	private final Crawler<?> crawler;
	private final Mod mod;

	public DownloadModInBrowserTask(Crawler<?> crawler, Mod mod) {
		super("Downloading Mod in Browser");
		this.crawler = crawler;
		this.mod = mod;
	}

	@Override
	public boolean execute() throws Exception {
		ModUpdateData data = crawler.getModUpdateData();

		if (JOptionPane.showConfirmDialog(
				null,
				String.format(
						"%s v%s is available.%n" +
								"Would you like to download it?%n" +
								"%n" +
								"You currently have v%s",
								data.name, data.modVersion, mod.getModVersion()
						),
						"Update Tinker Time",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE
				) == JOptionPane.YES_OPTION){
			BrowserGoToTask.callNow(crawler.getDownloadLink());
			return true;
		}
		return false;
	}

	@Override
	protected int findTargetProgress() throws IOException {
		return 0;
	}

}
