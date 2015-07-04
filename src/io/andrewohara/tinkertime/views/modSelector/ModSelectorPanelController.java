package io.andrewohara.tinkertime.views.modSelector;

import io.andrewohara.common.views.DecoratedComponent;
import io.andrewohara.common.views.selectorPanel.SelectorPanelController;
import io.andrewohara.tinkertime.controllers.ModManager;
import io.andrewohara.tinkertime.controllers.ModUpdateHandler;
import io.andrewohara.tinkertime.models.Installation;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JSplitPane;
import javax.swing.Timer;

public class ModSelectorPanelController implements ModUpdateHandler, DecoratedComponent<JSplitPane> {

	private final SelectorPanelController<Mod> spc;
	private Installation cachedInstallation;

	public ModSelectorPanelController(SelectorPanelController<Mod> spc, ModManager mm){
		this.spc = spc;
		new DropTarget(spc.getList(), new DragDropHandler(spc.getList(), mm));

		// Start timer to refresh mod listing
		new Timer(100, new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshMods();
			}
		}).start();
	}

	@Override
	public void changeInstallation(Installation newInstallation){
		cachedInstallation = newInstallation;
		refreshMods();

	}

	@Override
	public JSplitPane getComponent() {
		return spc.getComponent();
	}

	public void refreshMods() {
		if (cachedInstallation != null){
			spc.setData(cachedInstallation.getMods());
		}
	}
}
