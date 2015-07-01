package io.andrewohara.tinkertime.controllers.workflows.tasks;

import io.andrewohara.common.workflows.tasks.WorkflowTask;
import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateCoordinatorImpl;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.io.IOException;

public class MarkModUpdatedTask extends WorkflowTask {

	ModUpdateCoordinatorImpl updateCoordinator;
	private final Mod mod;

	public MarkModUpdatedTask(ModUpdateCoordinatorImpl updateCoordinator, Mod mod) {
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
