package aohara.tinkertime.views;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.controllers.ModManager.CannotAddModException;

@SuppressWarnings("serial")
public class TinkerMenuBar extends JMenuBar {
	
	private final Component frame;
	private final ModManager mm;
	
	public TinkerMenuBar(Component frame, ModManager mm){
		this.frame = frame;
		this.mm = mm;
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new JMenuItem(new AddModAction()));
		add(fileMenu);
		
	}
	
	private class AddModAction extends AbstractAction {
		
		public AddModAction(){
			super("Add Mod");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// Get URL from user
			String url = JOptionPane.showInputDialog(
				frame,
				"Please enter the Curse.com URl of the mod you would like to"
				+ "add.\ne.g. http://www.curse.com/ksp-mods/kerbal/220221-mechjeb",
				"Enter Curse.com Mod URL",
				JOptionPane.QUESTION_MESSAGE
			);
			
			// Check if URL is valid
			try {
				URI uri = new URI(url);
				if (!uri.getHost().contains("curse.com")){
					throw new URISyntaxException(
						url,
						"Currently, only mods from curse.com are accepted.\n"
					);
				}
			} catch (URISyntaxException e2) {
				errorMessage(e2.getMessage());
				return;
			}
			
			// Try to add Mod
			try {
				mm.addNewMod(url);
			} catch (CannotAddModException e1) {
				errorMessage(
					"Error Extracting Mod Info From Page.\n"
					+ "Either Curse.com has been updated,"
					+ "or this is an invalid link."
				);
			}
		}
		
		private void errorMessage(String message){
			JOptionPane.showMessageDialog(
				frame, message, "Cannot Add Mod", JOptionPane.ERROR_MESSAGE
			);
		}
	}

}
