package io.andrewohara.tinkertime.controllers.workflows.tasks;

import io.andrewohara.common.version.Version;
import io.andrewohara.common.workflows.tasks.BrowserGoToTask;
import io.andrewohara.common.workflows.tasks.WorkflowTask;
import io.andrewohara.tinkertime.io.crawlers.Crawler;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.io.IOException;

import javax.swing.JOptionPane;

public class DownloadModInBrowserTask extends WorkflowTask {

	private final Crawler<?> crawler;
	private final Version currentVersion;
	
	public DownloadModInBrowserTask(Crawler<?> crawler, Version currentVersion) {
		super("Downloading Mod in Browser");
		this.crawler = crawler;
		this.currentVersion = currentVersion;
	}

	@Override
	public boolean execute() throws Exception {
		Mod result = crawler.getUpdatedMod();
		
		if (JOptionPane.showConfirmDialog(
			null,
			String.format(
				"%s v%s is available.%n" +
				"Would you like to download it?%n" +
				"%n" + 
				"You currently have v%s",
				result.getName(), result.getModVersion(), currentVersion
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
