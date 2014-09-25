package aohara.tinkertime.controllers.fileUpdater;

import java.io.IOException;
import java.nio.file.Path;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.tasks.BrowserGoToTask;
import aohara.tinkertime.TinkerTime;
import aohara.tinkertime.controllers.WorkflowRunner;
import aohara.tinkertime.crawlers.Constants;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.workflows.ModWorkflowBuilder;

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
	public void buildWorkflowTask(Workflow workflow, Crawler<?> crawler) throws IOException {
		workflow.addTask(new BrowserGoToTask(workflow, ModWorkflowBuilder.downloadLinkGen(crawler)));
	}
}
