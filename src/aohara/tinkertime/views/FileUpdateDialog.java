package aohara.tinkertime.views;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import aohara.tinkertime.Config;
import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.models.UpdateListener;
import aohara.tinkertime.models.pages.FilePage;
import aohara.tinkertime.workflows.CheckForUpdateWorkflow;

@SuppressWarnings("serial")
public class FileUpdateDialog extends JDialog implements UpdateListener {
	
	private String latestVersion = null;
	private final Config config;
	private final ModManager mm;
	
	public FileUpdateDialog(String name, Config config, ModManager mm){
		this.config = config;
		this.mm = mm;
		
		setLayout(new GridLayout(2,2));
		setTitle(name);
		
		add(new JLabel(String.format("Latest Version: %s", latestVersion)));
		add(new JButton(new CheckForUpdatesAction(this)));
		
		add(new JLabel("Current Version: " + getCurrentFileName()));
		add(new JButton("Update"));
		
		pack();
	}
	
	private String getCurrentFileName(){
		for (File file : config.getGameDataPath().toFile().listFiles()){
			if (file.getName().startsWith("ModuleManager")){
				return file.getName();
			}
		}
		return null;
	}
	
	private class CheckForUpdatesAction extends AbstractAction {
		
		private final UpdateListener listener;
		
		public CheckForUpdatesAction(UpdateListener listener){
			super("Check for Updates");
			this.listener = listener;
		}

		@Override
		public void actionPerformed(ActionEvent e) {			
			try {
				mm.checkForUpdate(new CheckForUpdateWorkflow(
					"Module Manager",
					new URL("https://ksp.sarbian.com/jenkins/job/ModuleManager/lastSuccessfulBuild/api/json"),
					null,
					getCurrentFileName(),
					listener
				));
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	@Override
	public void setUpdateAvailable(FilePage latest){
		latestVersion = latest.getNewestFileName();
	}
}
