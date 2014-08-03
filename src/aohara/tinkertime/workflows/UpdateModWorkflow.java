package aohara.tinkertime.workflows;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import aohara.common.workflows.Workflow;
import aohara.tinkertime.Config;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.workflows.tasks.DownloadModFromPageTask;

public class UpdateModWorkflow extends Workflow {
	
	private final Path tempPagePath;

	public UpdateModWorkflow(URL url, Config config, ModStateManager sm) {
		super("Adding New Mod: " + url);
		
		try {
			tempPagePath = Files.createTempFile("page", ".temp");
			tempPagePath.toFile().deleteOnExit();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		// Add Tasks
		queueDownload(url, tempPagePath);
		addTask(new DownloadModFromPageTask(this, config, tempPagePath, url, sm));
	}
}
