package aohara.tinkertime.workflows;

import aohara.common.workflows.Workflow;
import aohara.tinkertime.Config;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModStructure;
import aohara.tinkertime.models.ModStructure.Module;
import aohara.tinkertime.workflows.tasks.DeleteModTask;

public class DeleteModWorkflow extends Workflow {

	public DeleteModWorkflow(Mod mod, Config config, ModStateManager sm) {
		super("Deleting " + mod.getName());
		if (mod.isEnabled()){
			for (Module module : new ModStructure(mod, config).getModules()){
				queueDelete(config.getGameDataPath().resolve(module.getName()));
			}
		}
		addTask(new DeleteModTask(this, mod, config, sm));
	}
}
