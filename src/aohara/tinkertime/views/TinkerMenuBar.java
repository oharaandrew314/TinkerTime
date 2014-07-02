package aohara.tinkertime.views;

import java.awt.event.ActionEvent;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import aohara.common.selectorPanel.ListListener;
import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.controllers.ModManager.CannotAddModException;
import aohara.tinkertime.controllers.ModManager.CannotDisableModException;
import aohara.tinkertime.models.Mod;

@SuppressWarnings("serial")
public class TinkerMenuBar extends JMenuBar implements ListListener<Mod>{
	
	private final ModManager mm;
	private Mod selectedMod;
	
	public TinkerMenuBar(ModManager mm){
		this.mm = mm;
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new JMenuItem(new AddModAction()));
		fileMenu.add(new JMenuItem(new DeleteModAction()));
		add(fileMenu);
	}
	
	private void errorMessage(String message){
		JOptionPane.showMessageDialog(
			getParent(), message, "Cannot Add Mod",
			JOptionPane.ERROR_MESSAGE);
	}
	
	private class AddModAction extends AbstractAction {
		
		public AddModAction(){
			super("Add Mod");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// Get URL from user
			String url = JOptionPane.showInputDialog(
				getParent(),
				"Please enter the Curse.com URl of the mod you would like to"
				+ "add.\ne.g. http://www.curse.com/ksp-mods/kerbal/220221-mechjeb",
				"Enter Curse.com Mod URL",
				JOptionPane.QUESTION_MESSAGE
			);
			
			// Cancel if not input given
			if (url == null || url.trim().isEmpty()){
				return;
			}
			
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
	}
	
	private class DeleteModAction extends AbstractAction {
		
		public DeleteModAction(){
			super("Delete");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (selectedMod != null){
				try {
					assert selectedMod != null;
					mm.deleteMod(selectedMod);
				} catch (CannotDisableModException e1) {
					errorMessage(selectedMod.getName() + " could not be disabled.");
				}
			}
		}
		
	}

	@Override
	public void elementClicked(Mod element, int numTimes) {
		// Do Nothing
	}

	@Override
	public void elementSelected(Mod element) {
		selectedMod = element;
	}

}
