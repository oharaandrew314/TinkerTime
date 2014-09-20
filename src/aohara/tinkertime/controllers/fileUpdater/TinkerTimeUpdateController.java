package aohara.tinkertime.controllers.fileUpdater;

import java.io.IOException;
import java.nio.file.Path;

import aohara.common.Util;
import aohara.tinkertime.TinkerTime;
import aohara.tinkertime.controllers.WorkflowRunner;
import aohara.tinkertime.controllers.crawlers.Constants;
import aohara.tinkertime.controllers.crawlers.Crawler;
import aohara.tinkertime.controllers.crawlers.CrawlerFactory;

public class TinkerTimeUpdateController extends FileUpdateController {

	public TinkerTimeUpdateController(WorkflowRunner runner) {
		super(runner, "Tinker Time");
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
		Util.goToHyperlink(createCrawler().getDownloadLink());
	}

	@Override
	protected Crawler<?> createCrawler() {
		return new CrawlerFactory().getCrawler(Constants.getTinkerTimeGithubUrl());
	}

}
