package aohara.tinkertime.controllers.coordinators;

import aohara.common.workflows.tasks.TaskCallback;
import aohara.common.workflows.tasks.WorkflowTask.TaskEvent;
import aohara.tinkertime.controllers.ModLoader;
import aohara.tinkertime.models.Installation;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.views.modSelector.ModListCellRenderer;
import aohara.tinkertime.views.modSelector.ModSelectorPanelFactory;

import com.google.inject.Singleton;

@Singleton
public class ModUpdateCoordinator extends TaskCallback implements ModUpdateHandler {

	private ModSelectorPanelFactory modSelectorPanelFactory;
	private ModLoader modLoader;
	private ModListCellRenderer modListCellRenderer;

	public void setup(ModSelectorPanelFactory modSelectorPanelFactory, ModLoader modLoader, ModListCellRenderer modListCellRender){
		this.modSelectorPanelFactory = modSelectorPanelFactory;
		this.modLoader = modLoader;
		this.modListCellRenderer = modListCellRender;
	}

	@Override
	public void changeInstallation(Installation newInstallation){
		modLoader.changeInstallation(newInstallation);
	}

	@Override
	public void updateMod(Mod mod) {
		modSelectorPanelFactory.get().updateMod(mod);
		modLoader.updateMod(mod);
	}

	@Override
	public void deleteMod(Mod mod) {
		modSelectorPanelFactory.get().deleteMod(mod);
		modLoader.deleteMod(mod);
	}

	@Override
	protected void processTaskEvent(TaskEvent event) {
		modListCellRenderer.handleTaskEvent(event);
	}

}
