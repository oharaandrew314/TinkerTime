package aohara.tinkertime.workflows.tasks;

import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.WorkflowTask;
import aohara.tinkertime.Config;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.models.Mod;

public class DeleteModTask extends WorkflowTask {
	
	private final Path zipPath;
	private final ModStateManager sm;
	private final Mod mod;

	public DeleteModTask(Workflow workflow, Mod mod, Config config, ModStateManager sm) {
		super(workflow);
		zipPath = config.getModZipPath(mod);
		this.sm = sm;
		this.mod = mod;
	}

	@Override
	public Boolean call() throws Exception {
		FileUtils.deleteQuietly(zipPath.toFile());
		sm.modUpdated(mod, true);
		return true;
	}

	@Override
	protected int getTargetProgress() throws InvalidContentException {
		return 1;
	}
}
