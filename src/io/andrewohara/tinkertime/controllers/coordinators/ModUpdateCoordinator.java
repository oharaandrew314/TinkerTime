package io.andrewohara.tinkertime.controllers.coordinators;

import io.andrewohara.common.workflows.tasks.TaskCallback;
import io.andrewohara.common.workflows.tasks.WorkflowTask.TaskEvent;
import io.andrewohara.tinkertime.controllers.ModLoader;
import io.andrewohara.tinkertime.models.Installation;
import io.andrewohara.tinkertime.models.Mod;
import io.andrewohara.tinkertime.views.modSelector.ModListCellRenderer;
import io.andrewohara.tinkertime.views.modSelector.ModSelectorPanelFactory;

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
