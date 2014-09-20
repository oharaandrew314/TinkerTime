package aohara.tinkertime.controllers.fileUpdater;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import aohara.common.workflows.TaskListener;
import aohara.common.workflows.Workflow;
import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.Config;
import aohara.tinkertime.controllers.WorkflowRunner;
import aohara.tinkertime.controllers.crawlers.Constants;
import aohara.tinkertime.controllers.crawlers.Crawler;
import aohara.tinkertime.controllers.crawlers.CrawlerFactory;
import aohara.tinkertime.workflows.CrawlerDownloadFileWorkflow;

public class ModuleManagerUpdateController extends FileUpdateController implements TaskListener {
	
	public static final String MODULE_MANAGER = "ModuleManager";
	private final Path destFolder;
	
	public ModuleManagerUpdateController(WorkflowRunner runner, Config config) {
		super(runner, "Module Manager");
		destFolder = config.getGameDataPath();
	}

	@Override
	public String getCurrentVersion() {
		Path path = getCurrentPath();
		if (path != null){
			return path.toFile().getName();
		}
		return null;
	}

	@Override
	public Path getCurrentPath() {
		for (File file : destFolder.toFile().listFiles()){
			if (file.getName().toLowerCase().startsWith(MODULE_MANAGER.toLowerCase())){
				return file.toPath();
			}
		}
		return null;
	}

	@Override
	public boolean currentlyExists() {
		return getCurrentPath() != null;
	}

	@Override
	public void update() throws IOException {
		if (currentlyExists()){
			getCurrentPath().toFile().delete();
		}
		Workflow workflow = new CrawlerDownloadFileWorkflow("Updating Module Manager", createCrawler(), destFolder);
		workflow.addListener(this);
		runner.submitDownloadWorkflow(workflow);
	}

	@Override
	protected Crawler<?> createCrawler() {
		URL url = Constants.getModuleManagerJenkinsUrl();
		return new CrawlerFactory().getCrawler(url);
	}
	
	@Override
	public void taskComplete(WorkflowTask task, boolean tasksRemaining) {
		updateDialog(null);
	}
	
	// -- Unused -----------------------------------------------------------
	
	@Override
	public void taskStarted(WorkflowTask task, int targetProgress) {
		// No Action
	}

	@Override
	public void taskProgress(WorkflowTask task, int increment) {
		// No Action
	}

	@Override
	public void taskError(WorkflowTask task, boolean tasksRemaining, Exception e) {
		// No Action
	}
}
