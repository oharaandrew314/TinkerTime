package io.andrewohara.tinkertime.controllers.coordinators;

import io.andrewohara.common.workflows.tasks.TaskCallback;
import io.andrewohara.common.workflows.tasks.WorkflowTask.TaskEvent;
import io.andrewohara.tinkertime.db.ModLoader;
import io.andrewohara.tinkertime.models.Installation;
import io.andrewohara.tinkertime.models.ModFile;
import io.andrewohara.tinkertime.models.mod.Mod;
import io.andrewohara.tinkertime.views.modSelector.ModListCellRenderer;
import io.andrewohara.tinkertime.views.modSelector.ModSelectorPanelFactory;

import java.sql.SQLException;
import java.util.Collection;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.j256.ormlite.dao.Dao;

@Singleton
public class ModUpdateCoordinator extends TaskCallback implements ModUpdateHandler {

	private final Dao<ModFile, Integer> modFilesDao;
	private final ModLoader modLoader;

	private ModSelectorPanelFactory modSelectorPanelFactory;
	private ModListCellRenderer modListCellRenderer;


	@Inject
	ModUpdateCoordinator(Dao<ModFile, Integer> modFilesDao, ModLoader modLoader){
		this.modFilesDao = modFilesDao;
		this.modLoader = modLoader;
	}

	public void setListeners(ModSelectorPanelFactory modSelectorPanelFactory, ModListCellRenderer modListCellRender){
		this.modSelectorPanelFactory = modSelectorPanelFactory;
		this.modListCellRenderer = modListCellRender;
	}

	@Override
	public void changeInstallation(Installation newInstallation){
		modLoader.changeInstallation(newInstallation);
		modSelectorPanelFactory.get().changeInstallation(newInstallation);
	}

	@Override
	public void updateMod(Mod mod) {
		modLoader.updateMod(mod);
		modSelectorPanelFactory.get().updateMod(mod);
	}

	@Override
	public void deleteMod(Mod mod) {
		modLoader.deleteMod(mod);
		modSelectorPanelFactory.get().deleteMod(mod);
	}

	@Override
	protected void processTaskEvent(TaskEvent event) {
		modListCellRenderer.handleTaskEvent(event);
	}

	public void updateModFiles(Mod mod, Collection<ModFile> modFiles){
		try {
			modFilesDao.delete(mod.getModFiles());
			for (ModFile newFile : modFiles){
				modFilesDao.create(newFile);
			}
			mod.setModFiles(modFiles);
			mod.setUpdateAvailable(false);
			updateMod(mod);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
