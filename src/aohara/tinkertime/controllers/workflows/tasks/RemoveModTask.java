package aohara.tinkertime.controllers.workflows.tasks;

import java.io.IOException;

import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.controllers.coordinators.ModUpdateCoordinator;
import aohara.tinkertime.models.Mod;

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
