package aohara.tinkertime.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import thirdParty.VerticalLayout;
import aohara.tinkertime.controllers.WorkflowRunner;
import aohara.tinkertime.controllers.fileUpdater.CurrentVersion;
import aohara.tinkertime.controllers.fileUpdater.FileDownloadController;
import aohara.tinkertime.models.UpdateListener;
import aohara.tinkertime.models.pages.FilePage;
import aohara.tinkertime.workflows.CheckForUpdateWorkflow;

@SuppressWarnings("serial")
public class FileUpdateDialog extends JDialog implements UpdateListener {
	
	private final WorkflowRunner runner;
	private final URL pageUrl;
	private final JLabel currentVersionLabel, latestVersionLabel;
	private final JButton updateButton;
	private final CurrentVersion currentVersion;
	
	public FileUpdateDialog(
			String name, WorkflowRunner runner, URL pageUrl,
			CurrentVersion currentVersion, FileDownloadController downloader){
		
		this.runner = runner;
		this.pageUrl = pageUrl;
		this.currentVersion = currentVersion;

		// Configure Dialog
		setTitle(name);
		setLayout(new VerticalLayout());
		setModal(true);
		setResizable(false);
		
		// Label Panel
		JPanel labelPanel = new JPanel(new VerticalLayout());
		labelPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		labelPanel.add(latestVersionLabel = new JLabel());
		labelPanel.add(currentVersionLabel = new JLabel());
		add(labelPanel);
		
		add(new JButton(new CheckForUpdateAction(this, downloader)));
		add(updateButton = new JButton(downloader));
		downloader.setFileUpdateDialog(this);
		
		// Initialize components
		setUpdateAvailable(null);
		updateCurrentVersion();
		updateButton.setEnabled(false);
		
		pack();
	}
	
	@Override
	public void setUpdateAvailable(FilePage latestPage){
		String latestVersion = latestPage != null ? latestPage.getNewestFileName() : "Please Check";
		latestVersionLabel.setText(String.format("Latest Version: %s", latestVersion));
		
		if (latestPage != null){
			updateButton.setEnabled(true);
		}
	}
	
	public void updateCurrentVersion(){
		String current = currentVersion.getVersion();
		currentVersionLabel.setText(String.format(
			"Current Version: %s", current != null ? current : "Not Installed"
		));
	}
	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(300, 160);
	}
	
	// -- Actions ---------------------------------------------------------
	
	private class CheckForUpdateAction extends AbstractAction {
		
		private final UpdateListener[] listeners;
		
		private CheckForUpdateAction(UpdateListener... listeners){
			super("Check for Updates");
			this.listeners = listeners;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String current = currentVersion.getVersion();
			runner.submitDownloadWorkflow(new CheckForUpdateWorkflow(
				getTitle(), pageUrl, null, current, false, listeners
			));
		}
	}
}
