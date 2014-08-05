package aohara.tinkertime.views;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import aohara.common.workflows.TaskListener;
import aohara.common.workflows.Workflow;
import aohara.common.workflows.Workflows;
import aohara.common.workflows.tasks.WorkflowTask;
import aohara.tinkertime.Config;
import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.models.UpdateListener;
import aohara.tinkertime.models.pages.FilePage;
import aohara.tinkertime.workflows.CheckForUpdateWorkflow;

@SuppressWarnings("serial")
public class FileUpdateDialog extends JDialog implements UpdateListener {
	
	private final Config config;
	private final ModManager mm;
	private final URL pageUrl;
	private final JLabel currentVersionLabel, latestVersionLabel;
	private final JButton updateButton;
	private FilePage latestPage;
	
	public FileUpdateDialog(String name, Config config, ModManager mm, URL pageUrl){
		this.config = config;
		this.mm = mm;
		this.pageUrl = pageUrl;
		
		setLayout(new GridLayout(2,2));
		setTitle(name);
		
		add(latestVersionLabel = new JLabel());
		setUpdateAvailable(null);
		add(new JButton(new CheckForUpdateAction(this)));
		
		add(currentVersionLabel = new JLabel());
		updateCurrentVersion();
		add(updateButton = new JButton(new UpdateAction()));
		updateButton.setEnabled(false);
		
		pack();
	}
	
	private File getCurrentFile(){
		for (File file : config.getGameDataPath().toFile().listFiles()){
			if (file.getName().startsWith("ModuleManager")){
				return file;
			}
		}
		return null;
	}
	
	@Override
	public void setUpdateAvailable(FilePage latestPage){
		this.latestPage = latestPage;
		String latestVersion = latestPage != null ? latestPage.getNewestFileName() : "Please Check";
		latestVersionLabel.setText(String.format("Latest Version: %s", latestVersion));
		
		File currentFile = getCurrentFile();
		if (latestPage != null &&
			(currentFile ==  null ||
			!latestPage.getNewestFileName().equals(currentFile.getName()))
		){
			this.latestPage = latestPage;
			updateButton.setEnabled(true);
		}
	}
	
	public void updateCurrentVersion(){
		File currentFile = getCurrentFile();
		currentVersionLabel.setText(String.format(
			"Current Version: %s",
			currentFile != null ? currentFile.getName() : "Not Installed"
		));
	}
	
	// -- Actions ---------------------------------------------------------
	
	private class CheckForUpdateAction extends AbstractAction {
		
		private final UpdateListener listener;
		
		private CheckForUpdateAction(UpdateListener listener){
			super("Check for Updates");
			this.listener = listener;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			File currentFile = getCurrentFile();
			mm.submitDownloadWorkflow(new CheckForUpdateWorkflow(
				getTitle(),
				pageUrl,
				null,
				currentFile != null ? getCurrentFile().getName(): null,
				false,
				listener
			));
		}
	}
	
	private class UpdateAction extends AbstractAction implements TaskListener {
		
		private UpdateAction(){
			super("Update");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				String latestFileName = latestPage.getNewestFileName();
				
				Workflow workflow = Workflows.tempDownload(
					latestPage.getDownloadLink(),
					config.getGameDataPath().resolve(latestFileName)
				);
				
				File currentFile = getCurrentFile();
				if (currentFile != null && !currentFile.getName().equals(latestFileName)){
					workflow.queueDelete(currentFile.toPath());
				}
				workflow.addListener(this);
				mm.submitDownloadWorkflow(workflow);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		@Override
		public void taskStarted(WorkflowTask task, int targetProgress) {
			// No Action
		}

		@Override
		public void taskProgress(WorkflowTask task, int increment) {
			// No Action
		}

		@Override
		public void taskError(WorkflowTask task, boolean tasksRemaining,
				Exception e) {
			// No Action
		}

		@Override
		public void taskComplete(WorkflowTask task, boolean tasksRemaining) {
			updateCurrentVersion();
		}
	}
}
