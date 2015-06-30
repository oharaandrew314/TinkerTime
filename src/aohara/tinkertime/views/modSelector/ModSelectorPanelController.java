package aohara.tinkertime.views.modSelector;

import java.awt.dnd.DropTarget;

import javax.swing.JSplitPane;

import aohara.common.views.selectorPanel.DecoratedComponent;
import aohara.common.views.selectorPanel.SelectorPanelController;
import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.controllers.coordinators.ModUpdateHandler;
import aohara.tinkertime.models.Installation;
import aohara.tinkertime.models.Mod;

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
	public void changeInstallation(Installation newInstallation){
		spc.setData(newInstallation.getMods());
	}

	@Override
	public JSplitPane getComponent() {
		return spc.getComponent();
	}
}
