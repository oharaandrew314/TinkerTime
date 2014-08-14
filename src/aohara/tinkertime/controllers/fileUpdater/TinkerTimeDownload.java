package aohara.tinkertime.controllers.fileUpdater;

import java.io.IOException;

import aohara.common.Util;
import aohara.tinkertime.models.pages.FilePage;

@SuppressWarnings("serial")
public class TinkerTimeDownload extends FileDownloadController {

	@Override
	protected void download(FilePage latestPage) throws IOException {
		Util.goToHyperlink(latestPage.getDownloadLink());
	}
}
