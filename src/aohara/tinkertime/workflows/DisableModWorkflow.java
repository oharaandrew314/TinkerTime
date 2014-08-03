package aohara.tinkertime.workflows;

import aohara.common.workflows.Workflow;
import aohara.tinkertime.Config;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModStructure;
import aohara.tinkertime.models.ModStructure.Module;
import aohara.tinkertime.workflows.tasks.MarkModEnabledTask;

public class DisableModWorkflow extends Workflow {
	
	public DisableModWorkflow(Mod mod, Config config, ModStateManager sm) {
		super("Disabling " + mod.getName());
		
		ModStructure structure = new ModStructure(mod, config);
		for (Module module : structure.getModules()){
			
			if (!isDependency(structure, module, config, sm)){
				queueDelete(config.getGameDataPath().resolve(module.getName()));
			}
		}
		addTask(new MarkModEnabledTask(this, mod, sm, false));
	}
	
	private boolean isDependency(ModStructure struct, Module module, Config config, ModStateManager sm){
		int numDependencies = 0;
		for (Mod mod : sm.getMods()){
			if (new ModStructure(mod, config).usesModule(module)){
				numDependencies++;
			}
		}
		return numDependencies > 1;
	}

}
