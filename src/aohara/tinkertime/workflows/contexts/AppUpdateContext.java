package aohara.tinkertime.workflows.contexts;

import java.io.IOException;

import javax.swing.JOptionPane;

import aohara.common.workflows.tasks.BrowserGoToTask;
import aohara.tinkertime.TinkerTime;
import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.crawlers.Constants;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.VersionInfo;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.models.FileUpdateListener;
import aohara.tinkertime.workflows.ModWorkflowBuilder;

/**
 * Used to receive the callback when an app update is available.
 * 
 * When the callback is received, the context will give the user a choice
 * to download the latest version with their web browser.
 * 
 * @author Andrew O'Hara
 */
public class AppUpdateContext implements FileUpdateListener {
	
	public static void checkForUpdates(ModManager mm) throws UnsupportedHostException {
		ModWorkflowBuilder builder = new ModWorkflowBuilder("Updating " + TinkerTime.NAME);
		VersionInfo currentVersion = new VersionInfo(TinkerTime.VERSION, null, TinkerTime.FULL_NAME);
		builder.checkForUpdates(Constants.getTinkerTimeGithubUrl(), currentVersion, new AppUpdateContext());
		mm.submitDownloadWorkflow(builder.buildWorkflow());
	}
	
	@Override
	public void setUpdateAvailable(Crawler<?> crawler) {
		try {
			if (JOptionPane.showConfirmDialog(
				null,
				String.format(
					"%s v%s is available.\n" +
					"Would you like to download it?\n" +
					"\n" + 
					"You currently have v%s",
					TinkerTime.NAME, crawler.getVersion(), TinkerTime.VERSION
				),
				
				
				//String.format("Current: %s\nAvailable: %s\nDownload?", TinkerTime.VERSION, crawler.getVersion()),
				"Update Tinker Time",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE
			) == JOptionPane.YES_OPTION){
				new BrowserGoToTask(crawler.getDownloadLink()).call(null);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
