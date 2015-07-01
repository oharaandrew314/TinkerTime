package io.andrewohara.tinkertime.launcher;

import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateCoordinator;
import io.andrewohara.tinkertime.views.modSelector.ModListCellRenderer;
import io.andrewohara.tinkertime.views.modSelector.ModSelectorPanelFactory;

import com.google.inject.Inject;

class SetupListeners implements Runnable {

	private final ModUpdateCoordinator modUpdateCoordinator;
	private final ModSelectorPanelFactory modSelectorPanelFactory;
	private final ModListCellRenderer modListCellRenderer;

	@Inject
	SetupListeners(ModUpdateCoordinator modUpdateCoordinator, ModSelectorPanelFactory modSelectorPanelFactory, ModListCellRenderer modListCellRender) {
		this.modUpdateCoordinator = modUpdateCoordinator;

		this.modSelectorPanelFactory = modSelectorPanelFactory;
		this.modListCellRenderer = modListCellRender;
	}

	@Override
	public void run() {
		modUpdateCoordinator.setListeners(modSelectorPanelFactory, modListCellRenderer);
	}
}
