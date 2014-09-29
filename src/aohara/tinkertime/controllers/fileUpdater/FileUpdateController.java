package aohara.tinkertime.controllers.fileUpdater;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import aohara.common.workflows.TaskListener;
import aohara.common.workflows.Workflow;
import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.controllers.WorkflowRunner;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.CrawlerFactory;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.models.FileUpdateListener;
import aohara.tinkertime.models.UpdateableFile;
import aohara.tinkertime.views.FileUpdateDialog;
import aohara.tinkertime.workflows.ModWorkflowBuilder;

/**
 * Controller for managing a {@link aohara.views.FileUpdateDialog}.
 * 
 * @author Andrew O'Hara
 */
@SuppressWarnings("serial")
public abstract class FileUpdateController implements FileUpdateListener, TaskListener {
	
	private final WorkflowRunner runner;
	private FileUpdateDialog dialog;
	private final String title;
	private final URL pageUrl;
	
	protected FileUpdateController(WorkflowRunner runner, String title, URL pageUrl) {
		this.runner = runner;
		this.title = title;
		this.pageUrl = pageUrl;
	}
	
	public abstract String getCurrentVersion();
	public abstract Path getCurrentPath();
	
	public void showDialog(){
		dialog = new FileUpdateDialog(title, new UpdateAction(this), new CheckLatestAction(this));
		updateDialog(null);
		dialog.setVisible(true);
	}
	
	protected void updateDialog(String latestVersion){
		if (dialog != null){
			dialog.update(getCurrentVersion(), latestVersion);
		}
	}
	
	@Override
	public void setUpdateAvailable(URL pageUrl, String newestFileName) {
		updateDialog(newestFileName);
	}
	
	public void checkForUpdate() throws IOException, UnsupportedHostException{
		Workflow wf = new Workflow("Checking for update for " + title);
		ModWorkflowBuilder.checkLatestVersion(wf, new UpdateableFile(getCurrentVersion(), null, pageUrl), this);
		submitWorkflow(wf);
	}
	
	public void downloadUpdate(boolean onlyIfNewer) throws IOException, UnsupportedHostException{
		Workflow workflow = new Workflow("Updating " + title);
		buildWorkflowTask(workflow, new CrawlerFactory().getCrawler(pageUrl), onlyIfNewer);
		submitWorkflow(workflow);
	}
	
	private void submitWorkflow(Workflow workflow){
		workflow.addListener(this);
		runner.submitDownloadWorkflow(workflow);
	}
	
	public abstract void buildWorkflowTask(Workflow workflow, Crawler<?> crawler, boolean downloadOnlyIfNewer) throws IOException;
	
	// -- Actions ------------------------------------------------------------
	
	/**
	 * Orders the FileUpdateController to update to the latest version.
	 * 
	 * @author Andrew O'Hara
	 */
	private static class UpdateAction extends AbstractAction {
		
		private final FileUpdateController controller;
		
		public UpdateAction(FileUpdateController controller){
			super("Update");
			this.controller = controller;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				controller.downloadUpdate(false);
			} catch (IOException | UnsupportedHostException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(controller.dialog, "Error Updating\n\n" + e1.toString());
			}
		}
	}
	
	/**
	 * Checks for the latest version and reports it back when completed.
	 * 
	 * The FileUpdateController is passed in as a {@link aohara.tinkertime.models.FileUpdateListener}.
	 * 
	 * @author Andrew O'Hara
	 */
	private static class CheckLatestAction extends AbstractAction {
		
		private final FileUpdateController controller;
		
		public CheckLatestAction(FileUpdateController controller){
			super("Check for Updates");
			this.controller = controller;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				controller.checkForUpdate();
			} catch (IOException | UnsupportedHostException e1) {
				JOptionPane.showMessageDialog(controller.dialog, "Error Checking for Updates\n\n" + e1.toString());
			}
		}
	}
	
	// -- Listeners -----------------------------------------------------

	@Override
	public void taskComplete(WorkflowTask task, boolean tasksRemaining) {
		updateDialog(null);
	}
	
	// -- Unused -----------------------------------------------------------
	
	@Override
	public void taskStarted(WorkflowTask task, int targetProgress) {
		// No Action
	}

	@Override
	public void taskProgress(WorkflowTask task, int increment) {
		// No Action
	}

	@Override
	public void taskError(WorkflowTask task, boolean tasksRemaining, Exception e) {
		// No Action
	}
}
