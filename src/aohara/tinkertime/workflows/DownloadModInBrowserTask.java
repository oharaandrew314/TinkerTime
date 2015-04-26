package aohara.tinkertime.workflows;

import java.io.IOException;

import javax.swing.JOptionPane;

import com.github.zafarkhaja.semver.Version;

import aohara.common.workflows.tasks.BrowserGoToTask;
import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.models.Mod;

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
		Mod result = crawler.getMod();
		
		if (JOptionPane.showConfirmDialog(
			null,
			String.format(
				"%s v%s is available.%n" +
				"Would you like to download it?%n" +
				"%n" + 
				"You currently have v%s",
				result.name, result.getVersion(), currentVersion
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
