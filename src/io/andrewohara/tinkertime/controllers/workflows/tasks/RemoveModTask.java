package io.andrewohara.tinkertime.controllers.workflows.tasks;

import io.andrewohara.common.workflows.tasks.WorkflowTask;
import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateCoordinator;
import io.andrewohara.tinkertime.models.Mod;

import java.io.IOException;

public class RemoveModTask extends WorkflowTask {

	private final Mod mod;
	private final ModUpdateCoordinator updateCoordinator;

	public RemoveModTask(Mod mod, ModUpdateCoordinator updateCoordinator) {
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
