package io.andrewohara.tinkertime.controllers;

import io.andrewohara.common.Listenable;
import io.andrewohara.common.workflows.tasks.TaskCallback;
import io.andrewohara.tinkertime.controllers.ModExceptions.CannotDeleteModException;
import io.andrewohara.tinkertime.controllers.ModExceptions.ModNotDownloadedException;
import io.andrewohara.tinkertime.controllers.ModExceptions.ModUpdateFailedError;
import io.andrewohara.tinkertime.controllers.ModExceptions.NoModSelectedException;
import io.andrewohara.tinkertime.controllers.workflows.ModWorkflowBuilder;
import io.andrewohara.tinkertime.controllers.workflows.ModWorkflowBuilderFactory;
import io.andrewohara.tinkertime.io.crawlers.CrawlerFactory.UnsupportedHostException;
import io.andrewohara.tinkertime.launcher.TinkerTimeLauncher;
import io.andrewohara.tinkertime.models.DefaultMods;
import io.andrewohara.tinkertime.models.Mod;
import io.andrewohara.tinkertime.models.ModFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Controller for initiating Asynchronous Tasks for Mod Processing.
 *
 * All Mod-Related Actions are to be initiated through this Controller.
 * All Asynchronous tasks initiated are executed by the executors of this class,
 * and the tasks are represented by {@link io.andrewohara.common.workflows.Workflow} classes.
 *
 * @author Andrew O'Hara
 */
@Singleton
public class ModManager extends Listenable<TaskCallback> {

	private final TaskLauncher taskLauncher;
	private final ModWorkflowBuilderFactory workflowBuilderFactory;
	private final ModLoader modLoader;

	private Mod selectedMod;

	@Inject
	ModManager(ModWorkflowBuilderFactory workflowBuilderFactory, ModLoader modLoader, TaskLauncher taskLauncher){
		this.workflowBuilderFactory = workflowBuilderFactory;
		this.modLoader = modLoader;
		this.taskLauncher = taskLauncher;
	}

	// -- Interface --------------------------------------------------------

	public Mod getSelectedMod() throws NoModSelectedException {
		if (selectedMod == null){
			throw new NoModSelectedException();
		}
		return selectedMod;
	}

	public void selectMod(Mod mod){
		this.selectedMod = mod;
	}

	public void updateMod(Mod mod, boolean forceUpdate) throws ModUpdateFailedError, ModNotDownloadedException {
		if (!mod.isUpdateable()){
			throw new ModUpdateFailedError(mod, "Mod is a local zip only, and cannot be updated.");
		}
		try {
			ModWorkflowBuilder builder = workflowBuilderFactory.createBuilder();
			builder.updateMod(mod, forceUpdate);
			taskLauncher.submitDownloadWorkflow(builder, mod);
		} catch (UnsupportedHostException e) {
			throw new ModUpdateFailedError(e);
		}
	}

	public void downloadMod(URL url) throws MalformedURLException, UnsupportedHostException {
		ModWorkflowBuilder builder = workflowBuilderFactory.createBuilder();
		Mod tempMod = builder.downloadNewMod(url);
		taskLauncher.submitDownloadWorkflow(builder, tempMod);
	}

	public void addModZip(Path zipPath){
		ModWorkflowBuilder builder = workflowBuilderFactory.createBuilder();
		Mod tempMod = builder.addLocalMod(zipPath);
		taskLauncher.submitDownloadWorkflow(builder, tempMod);
	}

	public void updateMods() throws ModUpdateFailedError, ModNotDownloadedException{
		for (Mod mod : modLoader.getMods()){
			if (mod.isUpdateable()){
				updateMod(mod, false);
			}
		}
	}

	public void toggleMod(final Mod mod) {
		//FIXME not implemented
		/*
		ModWorkflowBuilder builder = workflowBuilderFactory.createBuilder();
		try {
			if (modLoader.isEnabled(mod)){
				builder.disableMod(mod);
			} else {
				builder.enableMod(mod);
			}
			submitEnablerWorkflow(builder, mod);
		} catch (ModNotDownloadedException e){
			// Ignore user input if mod not downloaded
		}
		 */
	}

	public void deleteMod(final Mod mod) throws CannotDeleteModException {
		if (DefaultMods.isBuiltIn(mod)){
			throw new CannotDeleteModException(mod, "Built-in");
		}
		ModWorkflowBuilder builder = workflowBuilderFactory.createBuilder();
		builder.deleteMod(mod);
		taskLauncher.submitFileWorkflow(builder, mod);
	}

	public void checkForModUpdates() throws UnsupportedHostException{  // TODO Throw multi-exception
		UnsupportedHostException e = null;

		for (final Mod mod : modLoader.getMods()){
			try {
				if (mod.isUpdateable()){
					ModWorkflowBuilder builder = workflowBuilderFactory.createBuilder();
					builder.checkForUpdates(mod, true);
					taskLauncher.submitDownloadWorkflow(builder, mod);
				}
			} catch (UnsupportedHostException ex) {
				ex.printStackTrace();
				e = ex;
			}
		}

		if (e != null){
			throw e;
		}
	}

	//FIXME Update to new version
	/*
	public void exportEnabledMods(Path path){
		modLoader.exportEnabledMods(path);
	}

	public void importMods(Path path){
		modLoader.importMods(path);
	}
	 */

	public void tryUpdateModManager() throws UnsupportedHostException, MalformedURLException {
		ModWorkflowBuilder builder = workflowBuilderFactory.createBuilder();
		Mod tempMod = ModFactory.newTempMod(new URL(TinkerTimeLauncher.DOWNLOAD_URL), TinkerTimeLauncher.VERSION);
		builder.checkForUpdates(tempMod, false);
		builder.downloadModInBrowser(tempMod);
		taskLauncher.submitDownloadWorkflow(builder, tempMod);
	}

	public void reloadMods(){
		//configController.reloadMods();
		//TODO reimplement reloadMods
	}
}
