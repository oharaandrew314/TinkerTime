package aohara.tinkertime.workflows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import aohara.common.workflows.Workflow;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.workflows.tasks.MarkModUpdatedTask;

public class CheckForUpdateWorkflow extends Workflow {

	public CheckForUpdateWorkflow(Mod mod, ModStateManager sm) {
		super("Checking for Update to " + mod.getName());
		try {
			Path tempPagePath = Files.createTempFile("page", ".temp");
			tempPagePath.toFile().deleteOnExit();
			queueDownload(mod.getPageUrl(), tempPagePath);
			addTask(new MarkModUpdatedTask(this, mod, tempPagePath, sm));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
