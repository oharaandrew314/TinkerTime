package aohara.tinkertime.workflows;

import java.io.IOException;

import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.resources.ModLoader;

public class MarkModUpdatedTask extends WorkflowTask {
	
	private final ModLoader modLoader;
	private final Mod mod;

	public MarkModUpdatedTask(ModLoader modLoader, Mod mod) {
		super("Registering Available Update");
		this.modLoader = modLoader;
		this.mod = mod;
	}

	@Override
	public boolean execute() throws Exception {
		mod.updateAvailable = true;
		modLoader.modUpdated(mod);
		return true;
	}

	@Override
	protected int findTargetProgress() throws IOException {
		return 0;
	}

}
