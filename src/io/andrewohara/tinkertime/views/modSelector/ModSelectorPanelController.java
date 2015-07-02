package io.andrewohara.tinkertime.views.modSelector;

import io.andrewohara.common.views.selectorPanel.DecoratedComponent;
import io.andrewohara.common.views.selectorPanel.SelectorPanelController;
import io.andrewohara.tinkertime.controllers.ModManager;
import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateHandler;
import io.andrewohara.tinkertime.models.Installation;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.awt.dnd.DropTarget;

import javax.swing.JSplitPane;

public class ModSelectorPanelController implements ModUpdateHandler, DecoratedComponent<JSplitPane> {

	private final SelectorPanelController<Mod> spc;

	ModSelectorPanelController(SelectorPanelController<Mod> spc, ModManager mm){
		this.spc = spc;
		new DropTarget(spc.getList(), new DragDropHandler(spc.getList(), mm));
	}

	@Override
	public void updateMod(Mod mod) {
		spc.add(mod);
	}

	@Override
	public void deleteMod(Mod mod) {
		spc.remove(mod);
	}

	@Override
	public void reload(Installation newInstallation){
		spc.setData(newInstallation.getMods());
	}

	@Override
	public JSplitPane getComponent() {
		return spc.getComponent();
	}
}
