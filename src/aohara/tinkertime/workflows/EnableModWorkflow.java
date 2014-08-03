package aohara.tinkertime.workflows;

import aohara.common.workflows.ConflictResolver;
import aohara.common.workflows.Workflow;
import aohara.common.workflows.tasks.UnzipTask;
import aohara.tinkertime.Config;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModStructure;
import aohara.tinkertime.models.ModStructure.Module;
import aohara.tinkertime.workflows.tasks.MarkModEnabledTask;

public class EnableModWorkflow extends Workflow {

	public EnableModWorkflow(Mod mod, Config config, ModStateManager sm, ConflictResolver cr) {
		super("Enabling " + mod.getName());
		
		ModStructure structure = new ModStructure(mod, config);
		for (Module module : structure.getModules()){
			addTask(new UnzipTask(
				this, structure.zipPath,
				config.getGameDataPath(),
				module.getOutput(),
				cr));
		}
		addTask(new MarkModEnabledTask(this, mod, sm, true));
	}

}
