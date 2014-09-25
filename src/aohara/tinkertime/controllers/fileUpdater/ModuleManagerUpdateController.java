package aohara.tinkertime.controllers.fileUpdater;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

import aohara.common.workflows.TaskListener;
import aohara.common.workflows.Workflow;
import aohara.common.workflows.tasks.WorkflowTask;
import aohara.common.workflows.tasks.gen.PathGen;
import aohara.tinkertime.Config;
import aohara.tinkertime.controllers.WorkflowRunner;
import aohara.tinkertime.crawlers.Constants;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.workflows.ModWorkflowBuilder;

public class ModuleManagerUpdateController extends FileUpdateController implements TaskListener {
	
	public static final String MODULE_MANAGER = "ModuleManager";
	private final Path destFolder;
	
	public ModuleManagerUpdateController(WorkflowRunner runner, Config config) throws UnsupportedHostException {
		super(runner, "Module Manager", Constants.getModuleManagerJenkinsUrl());
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
		
		Workflow wf = new Workflow("Updating Module Manager");
		ModWorkflowBuilder.downloadFile(wf, crawler, getDestGen(crawler, destFolder));
		wf.addListener(this);
		runner.submitDownloadWorkflow(wf);
	}
	
	private PathGen getDestGen(final Crawler<?> crawler, final Path destFolder){
		return new PathGen(){
			@Override
			public URI getURI() throws URISyntaxException {
				return getPath().toUri();
			}

			@Override
			public Path getPath() {
				try {
					return destFolder.resolve(crawler.getNewestFileName());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
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
