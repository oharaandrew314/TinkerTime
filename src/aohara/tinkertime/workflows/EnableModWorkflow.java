package aohara.tinkertime.workflows;

import aohara.common.workflows.Workflow;
import aohara.tinkertime.models.Mod;

public class EnableModWorkflow extends Workflow {

	public EnableModWorkflow(Mod mod) {
		super("Enabling " + mod.getName());
	}

}
