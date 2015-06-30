package io.andrewohara.tinkertime.controllers.workflows.tasks;

import io.andrewohara.common.workflows.tasks.WorkflowTask;
import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateCoordinator;
import io.andrewohara.tinkertime.models.Mod;

import java.io.IOException;

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
		mod.setUpdateAvailable(true);
		updateCoordinator.updateMod(mod);
		return true;
	}

	@Override
	protected int findTargetProgress() throws IOException {
		return 0;
	}

}
