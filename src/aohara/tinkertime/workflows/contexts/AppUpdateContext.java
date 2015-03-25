package aohara.tinkertime.workflows.contexts;

import java.io.IOException;

import javax.swing.JOptionPane;

import aohara.common.workflows.tasks.BrowserGoToTask;
import aohara.tinkertime.TinkerTime;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.models.FileUpdateListener;

/**
 * Used to receive the callback when an app update is available.
 * 
 * When the callback is received, the context will give the user a choice
 * to download the latest version with their web browser.
 * 
 * @author Andrew O'Hara
 */
public class AppUpdateContext implements FileUpdateListener {
	
	public static final String APP_UPDATE_URL = "https://kerbalstuff.com/mod/243";
	
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
