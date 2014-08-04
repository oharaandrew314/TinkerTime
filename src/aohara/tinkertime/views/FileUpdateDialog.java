package aohara.tinkertime.views;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import aohara.tinkertime.Config;
import aohara.tinkertime.models.UpdateListener;
import aohara.tinkertime.models.pages.FilePage;

@SuppressWarnings("serial")
public class FileUpdateDialog extends JDialog implements UpdateListener {
	
	private String latestVersion = null;
	
	public FileUpdateDialog(String name, Config config){
		setLayout(new GridLayout(2,2));
		setTitle(name);
		
		add(new JLabel(String.format("Latest Version: %s", latestVersion)));
		add(new JButton(new CheckForUpdatesAction()));
		
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
	
	private class CheckForUpdatesAction extends AbstractAction {
		
		public CheckForUpdatesAction(){
			super("Check for Updates");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
		}
	}
	
	@Override
	public void setUpdateAvailable(FilePage latest){
		latestVersion = latest.getNewestFileName();
	}
}
