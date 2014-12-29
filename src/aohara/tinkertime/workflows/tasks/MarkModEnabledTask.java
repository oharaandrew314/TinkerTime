package aohara.tinkertime.workflows.tasks;

import java.io.IOException;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.Workflow.WorkflowTask;
import aohara.tinkertime.controllers.ModLoader;
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
	private final ModLoader sm;
	private final boolean markEnabled;

	public MarkModEnabledTask(Mod mod, ModLoader sm, boolean enable) {
		this.mod = mod;
		this.sm = sm;
		markEnabled = enable;
	}

	@Override
	public boolean call(Workflow workflow) throws Exception {
		mod.setEnabled(markEnabled);
		sm.modUpdated(mod);
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
