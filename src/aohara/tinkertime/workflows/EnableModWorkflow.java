package aohara.tinkertime.workflows;

import aohara.common.workflows.ConflictResolver;
import aohara.common.workflows.Workflow;
import aohara.common.workflows.tasks.UnzipTask;
import aohara.tinkertime.Config;
import aohara.tinkertime.content.ArchiveInspector;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModStructure;
import aohara.tinkertime.models.Module;
import aohara.tinkertime.workflows.tasks.MarkModEnabledTask;

/**
 * Workflow that will enable the given mod.
 * 
 * @author Andrew O'Hara
 */
public class EnableModWorkflow extends Workflow {

	public EnableModWorkflow(Mod mod, Config config, ModStateManager sm, ConflictResolver cr) {
		super("Enabling " + mod.getName());
		
		ModStructure structure = ArchiveInspector.inspectArchive(config, mod);
		for (Module module : structure.getModules()){
			addTask(new UnzipTask(
				this, structure.zipPath,
				config.getGameDataPath(),
				module.getContent(),
				cr));
		}
		addTask(new MarkModEnabledTask(this, mod, sm, true));
	}

}
