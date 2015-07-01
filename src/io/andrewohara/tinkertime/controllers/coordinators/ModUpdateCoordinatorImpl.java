package io.andrewohara.tinkertime.controllers.coordinators;

import io.andrewohara.common.workflows.tasks.TaskCallback;
import io.andrewohara.common.workflows.tasks.WorkflowTask.TaskEvent;
import io.andrewohara.tinkertime.db.ModLoader;
import io.andrewohara.tinkertime.models.Installation;
import io.andrewohara.tinkertime.models.ModFile;
import io.andrewohara.tinkertime.models.ModImage;
import io.andrewohara.tinkertime.models.Readme;
import io.andrewohara.tinkertime.models.mod.Mod;
import io.andrewohara.tinkertime.views.modSelector.ModListCellRenderer;
import io.andrewohara.tinkertime.views.modSelector.ModSelectorPanelFactory;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.j256.ormlite.dao.Dao;

@Singleton
public class ModUpdateCoordinatorImpl extends TaskCallback implements ModUpdateHandler, ModUpdateCoordinator {

	private final Dao<ModFile, Integer> modFilesDao;
	private final Dao<ModImage, Integer> modImagesDao;
	private final Dao<Readme, Integer> readmesDao;
	private final ModLoader modLoader;

	private ModSelectorPanelFactory modSelectorPanelFactory;
	private ModListCellRenderer modListCellRenderer;


	@Inject
	ModUpdateCoordinatorImpl(Dao<ModFile, Integer> modFilesDao, ModLoader modLoader, Dao<ModImage, Integer> modImagesDao, Dao<Readme, Integer> readmesDao){
		this.modFilesDao = modFilesDao;
		this.modImagesDao = modImagesDao;
		this.modLoader = modLoader;
		this.readmesDao = readmesDao;
	}

	@Override
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
		try {
			modImagesDao.delete(mod.getImage());
			modLoader.deleteMod(mod);
			modSelectorPanelFactory.get().deleteMod(mod);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void processTaskEvent(TaskEvent event) {
		modListCellRenderer.handleTaskEvent(event);
	}

	@Override
	public void updateModFiles(Mod mod, Collection<ModFile> modFiles, String readmeText){
		try {
			// Update Mod Files
			modFilesDao.delete(mod.getModFiles());
			for (ModFile newFile : modFiles){
				modFilesDao.create(newFile);
			}
			mod.setModFiles(modFiles);

			// Update ReadmeText
			Readme readme = mod.setReadmeText(readmeText);
			readmesDao.createOrUpdate(readme);

			updateMod(mod);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void updateModImage(Mod mod, BufferedImage image){
		try {
			ModImage modImage = ModImage.createModImage(mod, image);
			modImagesDao.createOrUpdate(modImage);

			mod.setImage(modImage);
			updateMod(mod);
		} catch (IOException | SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
