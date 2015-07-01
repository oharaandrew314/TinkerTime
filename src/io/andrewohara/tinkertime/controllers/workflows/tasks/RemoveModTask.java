package io.andrewohara.tinkertime.controllers.workflows.tasks;

import io.andrewohara.common.workflows.tasks.WorkflowTask;
import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateCoordinatorImpl;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.io.IOException;

public class RemoveModTask extends WorkflowTask {

	private final Mod mod;
	private final ModUpdateCoordinatorImpl updateCoordinator;

	public RemoveModTask(Mod mod, ModUpdateCoordinatorImpl updateCoordinator) {
		super("Removing " + mod);
		this.mod = mod;
		this.updateCoordinator = updateCoordinator;
	}

	@Override
	public boolean execute() throws Exception {
		updateCoordinator.deleteMod(mod);
		return true;
	}

	@Override
	protected int findTargetProgress() throws IOException {
		return 0;
	}

}
