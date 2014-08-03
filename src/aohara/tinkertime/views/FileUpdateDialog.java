package aohara.tinkertime.views;

import java.awt.GridLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import aohara.tinkertime.Config;

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
}
