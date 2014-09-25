package aohara.tinkertime.controllers.fileUpdater;

import java.io.IOException;
import java.nio.file.Path;

import aohara.common.Util;
import aohara.tinkertime.TinkerTime;
import aohara.tinkertime.controllers.WorkflowRunner;
import aohara.tinkertime.controllers.crawlers.Constants;
import aohara.tinkertime.controllers.crawlers.CrawlerFactory.UnsupportedHostException;

public class TinkerTimeUpdateController extends FileUpdateController {

	public TinkerTimeUpdateController(WorkflowRunner runner) throws UnsupportedHostException {
		super(runner, "Tinker Time", Constants.getTinkerTimeGithubUrl());
	}

	@Override
	public String getCurrentVersion() {
		return TinkerTime.VERSION;
	}

	@Override
	public Path getCurrentPath() {
		return null;
	}

	@Override
	public boolean currentlyExists() {
		return true;
	}

	@Override
	public void update() throws IOException {
		Util.goToHyperlink(crawler.getDownloadLink());
	}

}
