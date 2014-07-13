package aohara.tinkertime.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import thirdParty.VerticalLayout;
import aohara.tinkertime.TinkerTime;
import aohara.tinkertime.config.Config;
import aohara.tinkertime.config.Config.IllegalPathException;

@SuppressWarnings("serial")
public class DirectoryChooser extends JDialog {
	
	private static final String
		GAMEDATA_PATH = "Game Data Path",
		MODS_PATH = "Mods Path";
	
	private final boolean restartOnSuccess, exitOnCancel;
	private final Config config;
	private final PathPanel gameDataPanel, modsPanel;
	
	public DirectoryChooser(Config config, boolean restartOnSuccess, boolean exitOnCancel){
		this.restartOnSuccess = restartOnSuccess;
		this.exitOnCancel = exitOnCancel;
		this.config = config;
		
		setTitle(TinkerTime.NAME + " setup");
		setModal(true);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setLayout(new VerticalLayout());
		
		Path gameDataPath = config.getGameDataPath();
		Path modsPath = config.getModsPath();
		
		gameDataPanel = new PathPanel(
			GAMEDATA_PATH,
			"Please choose the path to the KSP GameData folder",
			JFileChooser.FILES_AND_DIRECTORIES,
			gameDataPath != null ? gameDataPath.toString() : null 
		);
		add(gameDataPanel);
		
		modsPanel = new PathPanel(
			MODS_PATH,
			"Please select a directory to store your mods in",
			JFileChooser.DIRECTORIES_ONLY,
			modsPath != null ? modsPath.toString() : null 
		);
		add(modsPanel);
		
		// South Controls
		JPanel controlPanel = new JPanel(new FlowLayout());
		add(controlPanel);
		controlPanel.add(new JButton(new CancelAction()));
		controlPanel.add(new JButton(new SaveAction(this)));
		
		pack();
	}
	
	// -- Panels -------------------------
	
	private class PathPanel extends JPanel {
		
		private final JTextField pathField;
		
		public PathPanel(String label, String description, int selectionType, String defaultText){
			super(new VerticalLayout());
			add(new JLabel(description));
			
			// Create Horizontal Panel
			JPanel selectorPanel = new JPanel(new BorderLayout());
			add(selectorPanel);
			
			// Add Label Text
			selectorPanel.add(new JLabel(label + ":"), BorderLayout.WEST);
			
			// Add Path Field
			JTextField textField = new JTextField(30);
			textField.setText(defaultText);
			selectorPanel.add(textField, BorderLayout.CENTER);
			
			// Add Selector Button
			ChooseAction action = new ChooseAction(
				label, description, textField, selectionType);
			pathField = action.pathField;
			selectorPanel.add(new JButton(action), BorderLayout.EAST);
		}
		
		public Path getPath(){
			String text = pathField.getText();
			return Paths.get(text != null ? text : "");
		}
	}
	
	// -- Actions ----------------------------
	
	private class ChooseAction extends AbstractAction {
		
		private final JFileChooser chooser = new JFileChooser();
		public final JTextField pathField;
		
		public ChooseAction(
			String label, String description, JTextField pathField,
			int selectionType
		){
			super("...");
			chooser.setDialogTitle(description);
			chooser.setApproveButtonText("Select " + label);
			chooser.setFileSelectionMode(selectionType);
			
			this.pathField = pathField;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
				pathField.setText(chooser.getSelectedFile().getPath());
			}
		}
	}
	
	private class CancelAction extends AbstractAction {
		
		public CancelAction() {
			super("Cancel");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (exitOnCancel){
				System.exit(0);
			} else {
				setVisible(false);
			}
		}
	}
	
	private class SaveAction extends AbstractAction {
		
		private final JDialog dialog;
		
		public SaveAction(JDialog dialog){
			super("Save");
			this.dialog = dialog;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				config.setModsPath(modsPanel.getPath());
				config.setGameDataPath(gameDataPanel.getPath());
				dialog.setVisible(false);
				if (restartOnSuccess){
					JOptionPane.showMessageDialog(
						dialog,
						"Please restart the application for changes to come into effect."
					);
					System.exit(0);
				}
			} catch (IllegalPathException e1) {
				JOptionPane.showMessageDialog(dialog, e1.getMessage());
			}
		}
		
	}
}
