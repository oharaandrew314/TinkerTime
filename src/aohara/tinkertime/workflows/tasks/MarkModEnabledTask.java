package aohara.tinkertime.workflows.tasks;

import java.io.IOException;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.models.Mod;

/**
 * Workflow Task that marks a mod as enabled.
 * 
 * This is done by notifiyng the mod and the ModStateManager.
 * This Task does NOT actually enable the mod.
 * 
 * @author Andrew O'Hara
 */
public class MarkModEnabledTask extends WorkflowTask {
	
	private final Mod mod;
	private final ModStateManager sm;
	private final boolean markEnabled;

	public MarkModEnabledTask(Workflow workflow, Mod mod, ModStateManager sm, boolean enable) {
		super(workflow);
		this.mod = mod;
		this.sm = sm;
		markEnabled = enable;
	}

	@Override
	public Boolean call() throws Exception {
		mod.setEnabled(markEnabled);
		sm.modUpdated(mod, false);
		return true;
	}

	@Override
	public int getTargetProgress() throws IOException {
		return -1;
	}

	@Override
	public String getTitle() {
		return String.format("Enabling %s", mod.getName());
	}

}
