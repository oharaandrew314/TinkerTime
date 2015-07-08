package io.andrewohara.tinkertime.controllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.sql.SQLException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.andrewohara.common.Listenable;
import io.andrewohara.common.workflows.tasks.TaskCallback;
import io.andrewohara.tinkertime.controllers.workflows.ModWorkflowBuilder;
import io.andrewohara.tinkertime.controllers.workflows.ModWorkflowBuilderFactory;
import io.andrewohara.tinkertime.controllers.workflows.TaskLauncher;
import io.andrewohara.tinkertime.io.crawlers.CrawlerFactory.UnsupportedHostException;
import io.andrewohara.tinkertime.models.mod.Mod;

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

	public void updateMod(Mod mod, boolean forceUpdate) throws ModUpdateFailedException {
		if (!mod.isUpdateable()){
			throw new ModUpdateFailedException(mod, "Mod is a local zip only, and cannot be updated.");
		}
		try {
			ModWorkflowBuilder builder = workflowBuilderFactory.createBuilder(mod);
			builder.updateMod(forceUpdate);
			if (mod.isEnabled()){
				builder.enableMod();
			}
			taskLauncher.submitDownloadWorkflow(builder);
		} catch (UnsupportedHostException | ModNotDownloadedException e) {
			throw new ModUpdateFailedException(e);
		}
	}

	public boolean downloadNewMod(URL url) throws SQLException, ModUpdateFailedException {
		if (modLoader.getByUrl(url) == null){
			Mod newMod = modLoader.newMod(url);
			updateMod(newMod, false);
			return true;
		}
		return false;
	}

	public void addModZip(Path zipPath) throws SQLException{
		Mod newMod = modLoader.newLocalMod(zipPath);
		ModWorkflowBuilder builder = workflowBuilderFactory.createBuilder(newMod);
		builder.addLocalMod(zipPath);
		taskLauncher.submitDownloadWorkflow(builder);
	}

	public void updateMods() throws ModUpdateFailedException, ModNotDownloadedException{
		for (Mod mod : modLoader.getMods()){
			if (mod.isUpdateable()){
				updateMod(mod, false);
			}
		}
	}

	public void toggleMod(final Mod mod) {
		ModWorkflowBuilder builder = workflowBuilderFactory.createBuilder(mod);
		try {
			if (mod.isEnabled()){
				builder.disableMod();
			} else {
				builder.enableMod();
			}
			taskLauncher.submitFileWorkflow(builder);
		} catch (ModNotDownloadedException e){
			// Ignore user input if mod not downloaded
		}
	}

	public void deleteMod(Mod mod) throws CannotDeleteModException {
		if (mod.isBuiltIn()){
			throw new CannotDeleteModException(mod, "Built-in");
		}
		ModWorkflowBuilder builder = workflowBuilderFactory.createBuilder(mod);
		builder.deleteMod();
		taskLauncher.submitFileWorkflow(builder);
	}

	public void checkForModUpdates() throws UnsupportedHostException{
		for (final Mod mod : modLoader.getMods()){
			try {
				if (mod.isUpdateable()){
					ModWorkflowBuilder builder = workflowBuilderFactory.createBuilder(mod);
					builder.checkForUpdates(true);
					taskLauncher.submitDownloadWorkflow(builder);
				}
			} catch (UnsupportedHostException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	public void tryUpdateModManager() throws UnsupportedHostException, MalformedURLException, SQLException {
		Mod tempMod = Mod.newModManagerMod();
		ModWorkflowBuilder builder = workflowBuilderFactory.createBuilder(tempMod);
		builder.checkForUpdates(false);
		builder.downloadModInBrowser(tempMod);
		taskLauncher.submitDownloadWorkflow(builder);
	}

	////////////////
	// Exceptions //
	////////////////

	@SuppressWarnings("serial")
	public static class ModNotDownloadedException extends Exception {
		public ModNotDownloadedException(Mod mod, String message){
			super("Error for " + mod + ": " + message);
		}
	}

	@SuppressWarnings("serial")
	public static class ModUpdateFailedException extends Exception {
		public ModUpdateFailedException(Exception e){
			super(e);
		}
		public ModUpdateFailedException(Mod mod, String message){
			super("Error for " + mod + ": " + message);
		}
	}

	@SuppressWarnings("serial")
	public static class NoModSelectedException extends Exception {

	}

	@SuppressWarnings("serial")
	public static class CannotDeleteModException extends Exception {
		public CannotDeleteModException(Mod mod, String reason){
			super(String.format("Cannot delete %s: %s", mod, reason));
		}
	}
}
