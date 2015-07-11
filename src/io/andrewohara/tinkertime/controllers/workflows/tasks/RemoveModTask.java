package io.andrewohara.tinkertime.controllers.workflows.tasks;

import io.andrewohara.common.workflows.tasks.WorkflowTask;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.io.IOException;

public class RemoveModTask extends WorkflowTask {

	private final Mod mod;

	public RemoveModTask(Mod mod) {
		super("Removing " + mod);
		this.mod = mod;
	}

	@Override
	public boolean execute() throws Exception {
		mod.delete();
		return true;
	}

	@Override
	protected int findTargetProgress() throws IOException {
		return 0;
	}

}
