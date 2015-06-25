package aohara.tinkertime.workflows;

import java.io.IOException;

import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.controllers.ModUpdateCoordinator;
import aohara.tinkertime.models.Mod;

public class MarkModUpdatedTask extends WorkflowTask {
	
	ModUpdateCoordinator updateCoordinator;
	private final Mod mod;

	public MarkModUpdatedTask(ModUpdateCoordinator updateCoordinator, Mod mod) {
		super("Registering Available Update");
		this.updateCoordinator = updateCoordinator;
		this.mod = mod;
	}

	@Override
	public boolean execute() throws Exception {
		mod.updateAvailable = true;
		updateCoordinator.modUpdated(this, mod);
		return true;
	}

	@Override
	protected int findTargetProgress() throws IOException {
		return 0;
	}

}
