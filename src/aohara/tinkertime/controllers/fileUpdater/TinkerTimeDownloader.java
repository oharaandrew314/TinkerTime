package aohara.tinkertime.controllers.fileUpdater;

import java.io.IOException;

import aohara.common.Util;
import aohara.tinkertime.controllers.crawlers.Crawler;

/**
 * Launches the Update of TinkerTime by starting a download in the user's browser.
 * 
 * Obtains the latest download link using a Github Crawler pointed at the
 * TinkerTime repo.
 * 
 * @author Andrew O'Hara
 */
@SuppressWarnings("serial")
public class TinkerTimeDownloader extends FileDownloadController {

	public TinkerTimeDownloader(Crawler<?, ?> crawler) {
		super(crawler);
	}

	@Override
	protected void download(Crawler<?, ?> crawler) throws IOException {
		Util.goToHyperlink(crawler.getDownloadLink());
	}
}
