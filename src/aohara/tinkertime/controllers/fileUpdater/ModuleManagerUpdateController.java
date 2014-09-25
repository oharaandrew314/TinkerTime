package aohara.tinkertime.controllers.fileUpdater;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.tasks.gen.PathGen;
import aohara.tinkertime.Config;
import aohara.tinkertime.controllers.WorkflowRunner;
import aohara.tinkertime.crawlers.Constants;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.models.UpdateableFile;
import aohara.tinkertime.workflows.ModWorkflowBuilder;

public class ModuleManagerUpdateController extends FileUpdateController {
	
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
	public void buildWorkflowTask(Workflow workflow, final Crawler<?> crawler, boolean downloadOnlyIfNewer) throws IOException {
		if (currentlyExists()){
			getCurrentPath().toFile().delete();
		}
		
		PathGen destGen = new PathGen(){
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
		
		if (downloadOnlyIfNewer){
			ModWorkflowBuilder.downloadFile(workflow, crawler, destGen);
		} else {
			try {
				ModWorkflowBuilder.downloadFileIfNewer(workflow, new UpdateableFile(getCurrentVersion(), null, crawler.url), destGen);
			} catch (UnsupportedHostException e) {
				throw new RuntimeException(e);
			}
		}
		
	}
}
