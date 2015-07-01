package io.andrewohara.tinkertime.controllers.coordinators;

import io.andrewohara.tinkertime.models.Installation;
import io.andrewohara.tinkertime.models.ModFile;
import io.andrewohara.tinkertime.models.mod.Mod;
import io.andrewohara.tinkertime.views.modSelector.ModListCellRenderer;
import io.andrewohara.tinkertime.views.modSelector.ModSelectorPanelFactory;

import java.awt.image.BufferedImage;
import java.util.Collection;

public interface ModUpdateCoordinator {

	public void setListeners(ModSelectorPanelFactory modSelectorPanelFactory, ModListCellRenderer modListCellRender);

	public void changeInstallation(Installation newInstallation);

	public void updateMod(Mod mod);

	public void deleteMod(Mod mod);

	public void updateModFiles(Mod mod, Collection<ModFile> modFiles, String readmeText);

	public void updateModImage(Mod mod, BufferedImage image);

}