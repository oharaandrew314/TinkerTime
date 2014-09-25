package aohara.tinkertime.controllers.fileUpdater;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import aohara.common.workflows.Workflow;
import aohara.tinkertime.controllers.WorkflowRunner;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.CrawlerFactory;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.models.FileUpdateListener;
import aohara.tinkertime.views.FileUpdateDialog;
import aohara.tinkertime.workflows.ModWorkflowBuilder;

/**
 * Controller for managing a {@link aohara.views.FileUpdateDialog}.
 * 
 * @author Andrew O'Hara
 *
 */
@SuppressWarnings("serial")
public abstract class FileUpdateController implements FileUpdateListener {
	
	protected final WorkflowRunner runner;
	private FileUpdateDialog dialog;
	private final String title;
	protected final Crawler<?> crawler;
	
	protected FileUpdateController(WorkflowRunner runner, String title, URL pageUrl) throws UnsupportedHostException{
		this.runner = runner;
		this.title = title;
		this.crawler = new CrawlerFactory().getCrawler(pageUrl);
	}
	
	public abstract String getCurrentVersion();
	public abstract Path getCurrentPath();
	public abstract boolean currentlyExists();
	public abstract void update() throws IOException;
	
	public void showDialog(){
		dialog = new FileUpdateDialog(title, new UpdateAction(), new CheckLatestAction(this));
		updateDialog(null);
		dialog.setVisible(true);
	}
	
	protected void updateDialog(String latestVersion){
		dialog.update(getCurrentVersion(), latestVersion);
	}
	
	@Override
	public void setUpdateAvailable(URL pageUrl, String newestFileName) {
		updateDialog(newestFileName);
	}
	
	// -- Actions ------------------------------------------------------------
	
	/**
	 * Orders the FileUpdateController to update to the latest version.
	 * 
	 * @author Andrew O'Hara
	 */
	private class UpdateAction extends AbstractAction {
		
		public UpdateAction(){
			super("Update");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				update();
			} catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error Updating\n\n" + e1.toString());
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
	private class CheckLatestAction extends AbstractAction {
		
		private final FileUpdateListener listener;
		
		public CheckLatestAction(FileUpdateListener listener){
			super("Check for Updates");
			this.listener = listener;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				Workflow wf = new Workflow("Checking for update for " + title);
				ModWorkflowBuilder.checkForUpdates(wf, crawler.url, null, getCurrentVersion(), listener);
				runner.submitDownloadWorkflow(wf);
			} catch (IOException | UnsupportedHostException e1) {
				e1.printStackTrace();
			}
			
		}
	}

}
