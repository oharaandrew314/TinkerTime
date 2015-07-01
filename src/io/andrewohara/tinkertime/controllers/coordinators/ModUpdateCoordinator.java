package io.andrewohara.tinkertime.controllers.coordinators;

import io.andrewohara.tinkertime.models.Installation;
import io.andrewohara.tinkertime.models.ModFile;
import io.andrewohara.tinkertime.models.mod.Mod;
import io.andrewohara.tinkertime.views.modSelector.ModListCellRenderer;
import io.andrewohara.tinkertime.views.modSelector.ModSelectorPanelFactory;

import java.util.Collection;

public interface ModUpdateCoordinator {

	public abstract void setListeners(
			ModSelectorPanelFactory modSelectorPanelFactory,
			ModListCellRenderer modListCellRender);

	public abstract void changeInstallation(Installation newInstallation);

	public abstract void updateMod(Mod mod);

	public abstract void deleteMod(Mod mod);

	public abstract void updateModFiles(Mod mod, Collection<ModFile> modFiles);

}