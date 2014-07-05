package aohara.tinkertime.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.util.HashMap;

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
		KERBAL_PATH = "Kerbal Path",
		MODS_PATH = "Mods Directory";
	
	private final HashMap<String, Path> paths = new HashMap<>();
	
	public DirectoryChooser(){
		setTitle(TinkerTime.NAME + " setup");
		setModal(true);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setLayout(new VerticalLayout());
		
		add(new InnerPanel(
			KERBAL_PATH,
			"Please choose the path to the KSP executable",
			JFileChooser.FILES_AND_DIRECTORIES
		));
		
		add(new InnerPanel(
			MODS_PATH,
			"Please select a directory to store your mods in",
			JFileChooser.DIRECTORIES_ONLY
		));
		
		// South Controls
		JPanel controlPanel = new JPanel(new FlowLayout());
		add(controlPanel);
		controlPanel.add(new JButton(new CancelAction()));
		controlPanel.add(new JButton(new SaveAction(this)));
		
		pack();
	}
	
	// -- Panels -------------------------
	
	private class InnerPanel extends JPanel {
		
		public InnerPanel(String label, String description, int selectionType){
			super(new VerticalLayout());
			add(new JLabel(description));
			
			// Create Horizontal Panel
			JPanel selectorPanel = new JPanel(new BorderLayout());
			add(selectorPanel);
			
			// Add Label Text
			selectorPanel.add(new JLabel(label + ":"), BorderLayout.WEST);
			
			// Add Path Field
			JTextField textField = new JTextField(30);
			selectorPanel.add(textField, BorderLayout.CENTER);
			
			// Add Selector Button
			selectorPanel.add(
				new JButton(new ChooseAction(
					label, description, textField, selectionType
				)),
				BorderLayout.EAST
			);
		}
	}
	
	// -- Actions ----------------------------
	
	private class ChooseAction extends AbstractAction {
		
		private final JFileChooser chooser = new JFileChooser();
		private final JTextField pathField;
		private final String label;
		
		public ChooseAction(
			String label, String description, JTextField pathField,
			int selectionType
		){
			super("...");
			chooser.setDialogTitle(description);
			chooser.setApproveButtonText("Select " + label);
			chooser.setFileSelectionMode(selectionType);
			
			this.label = label;
			this.pathField = pathField;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
				pathField.setText(chooser.getSelectedFile().getPath());
				paths.put(label, chooser.getSelectedFile().toPath());
			}
		}
	}
	
	private class CancelAction extends AbstractAction {
		
		public CancelAction() {
			super("Cancel");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
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
			Config config = new Config();
			try {
				config.setModsPath(paths.get(MODS_PATH));
				config.setKerbalPath(paths.get(KERBAL_PATH));
				dialog.setVisible(false);
			} catch (IllegalPathException e1) {
				JOptionPane.showMessageDialog(dialog, e1.getMessage());
			}
		}
		
	}
}
