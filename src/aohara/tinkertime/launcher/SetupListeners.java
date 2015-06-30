package aohara.tinkertime.launcher;

import aohara.tinkertime.controllers.ModLoader;
import aohara.tinkertime.controllers.coordinators.ModUpdateCoordinator;
import aohara.tinkertime.views.modSelector.ModListCellRenderer;
import aohara.tinkertime.views.modSelector.ModSelectorPanelFactory;

import com.google.inject.Inject;

class SetupListeners implements Runnable {

	private final ModUpdateCoordinator modUpdateCoordinator;
	private final ModSelectorPanelFactory modSelectorPanelFactory;
	private final ModLoader modLoader;
	private final ModListCellRenderer modListCellRenderer;

	@Inject
	SetupListeners(ModUpdateCoordinator modUpdateCoordinator, ModSelectorPanelFactory modSelectorPanelFactory, ModLoader modLoader, ModListCellRenderer modListCellRender) {
		this.modUpdateCoordinator = modUpdateCoordinator;

		this.modSelectorPanelFactory = modSelectorPanelFactory;
		this.modLoader = modLoader;
		this.modListCellRenderer = modListCellRender;
	}

	@Override
	public void run() {
		modUpdateCoordinator.setup(modSelectorPanelFactory, modLoader, modListCellRenderer);

	}

}
