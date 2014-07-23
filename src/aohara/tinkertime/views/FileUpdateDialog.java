package aohara.tinkertime.views;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import aohara.common.executors.Downloader;
import aohara.common.executors.context.ExecutorContext;
import aohara.common.executors.progress.ProgressListener;
import aohara.tinkertime.config.Config;

@SuppressWarnings("serial")
public class FileUpdateDialog extends JDialog {
	
	public FileUpdateDialog(String name, Config config){
		setLayout(new GridLayout(2,2));
		setTitle(name);
		
		add(new JLabel("Latest Version: Please Check"));
		add(new JButton("Check for Updates"));
		
		add(new JLabel("Current Version: " + getCurrentFileName(config)));
		add(new JButton("Update"));
		
		pack();
	}
	
	private String getCurrentFileName(Config config){
		for (File file : config.getGameDataPath().toFile().listFiles()){
			if (file.getName().startsWith("ModuleManager")){
				return file.getName();
			}
		}
		return null;
	}
	
	private class CheckForUpdatesAction extends AbstractAction
			implements ProgressListener {
		
		private final Downloader downloader;
		
		public CheckForUpdatesAction(Downloader downloader){
			this.downloader = downloader;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			PageUpdateContext context = new PageUpdateContext
		}

		@Override
		public void progressStarted(ExecutorContext context, int target,
				int tasksRunning) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void progressMade(ExecutorContext context, int current) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void progressComplete(ExecutorContext context, int tasksRunning) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void progressError(ExecutorContext context, int tasksRunning) {
			// TODO Auto-generated method stub
			
		}
		
	}

}
