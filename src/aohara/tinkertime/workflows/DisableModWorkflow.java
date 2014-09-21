package aohara.tinkertime.workflows;

import aohara.common.workflows.Workflow;
import aohara.tinkertime.Config;
import aohara.tinkertime.content.ArchiveInspector;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.Module;
import aohara.tinkertime.workflows.tasks.MarkModEnabledTask;

/**
 * Workflow that will result in the given mod being disabled.
 * 
 * @author Andrew O'Hara
 */
public class DisableModWorkflow extends Workflow {
	
	public DisableModWorkflow(Mod mod, Config config, ModStateManager sm) {
		super("Disabling " + mod.getName());
		
		for (Module module : ArchiveInspector.inspectArchive(config, mod).getModules()){
			
			if (!isDependency(module, config, sm)){
				queueDelete(config.getGameDataPath().resolve(module.getName()));
			}
		}
		addTask(new MarkModEnabledTask(this, mod, sm, false));
	}
	
	private boolean isDependency(Module module, Config config, ModStateManager sm){
		int numDependencies = 0;
		for (Mod mod : sm.getMods()){
			if (ArchiveInspector.inspectArchive(config, mod).usesModule(module)){
				numDependencies++;
			}
		}
		return numDependencies > 1;
	}

}
