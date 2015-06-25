package aohara.tinkertime.workflows.tasks;

import java.io.IOException;

import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.controllers.ModUpdateCoordinator;
import aohara.tinkertime.models.Mod;

public class RemoveModTask extends WorkflowTask {
	
	private final Mod mod;
	private final ModUpdateCoordinator updateCoordinator;

	public RemoveModTask(Mod mod, ModUpdateCoordinator updateCoordinator) {
		super("Removing " + mod.name);
		this.mod = mod;
		this.updateCoordinator = updateCoordinator;
	}

	@Override
	public boolean execute() throws Exception {
		updateCoordinator.modDeleted(this, mod);
		return true;
	}

	@Override
	protected int findTargetProgress() throws IOException {
		return 0;
	}

}
