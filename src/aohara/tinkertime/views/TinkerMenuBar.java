package aohara.tinkertime.views;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import aohara.common.selectorPanel.ListListener;
import aohara.tinkertime.config.Config;
import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.controllers.ModManager.CannotAddModException;
import aohara.tinkertime.controllers.ModManager.CannotDisableModException;
import aohara.tinkertime.controllers.ModManager.ModUpdateFailedException;
import aohara.tinkertime.controllers.DownloaderManager;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.models.Mod;

@SuppressWarnings("serial")
public class TinkerMenuBar extends JMenuBar implements ListListener<Mod>{
	
	private final ModManager mm;
	private final ModStateManager sm;
	private final DownloaderManager mpd;
	private Mod selectedMod;
	
	public TinkerMenuBar(ModManager mm, DownloaderManager mpd, ModStateManager sm){
		this.mm = mm;
		this.mpd = mpd;
		this.sm = sm;
		
		JMenu modMenu = new JMenu("Mod");
		modMenu.add(new JMenuItem(new AddModAction()));
		modMenu.add(new JMenuItem(new DeleteModAction()));
		add(modMenu);
		
		JMenu updateMenu = new JMenu("Update");
		updateMenu.add(new JMenuItem(new UpdateModAction()));
		updateMenu.add(new JMenuItem(new UpdateAllAction()));
		updateMenu.add(new JMenuItem(new CheckforUpdatesAction()));
		add(updateMenu);
		
		JMenuItem launchItem = new JMenuItem(new LaunchKspAction());
		add(launchItem);
	}
	
	private void errorMessage(String message){
		JOptionPane.showMessageDialog(
			getParent(), message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	/*
	private void successMessage(String message){
		JOptionPane.showMessageDialog(
			getParent(), message, "Success", JOptionPane.INFORMATION_MESSAGE);	
	}
	*/
	
	// -- Listeners --------------------------------------------------

		@Override
		public void elementClicked(Mod element, int numTimes) {
			// Do Nothing
		}

		@Override
		public void elementSelected(Mod element) {
			selectedMod = element;
		}
		
	// -- Actions ---------------------------------------------------
	
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
				+ " add.\ne.g. http://www.curse.com/ksp-mods/kerbal/220221-mechjeb",
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
					mm.deleteMod(selectedMod);
				} catch (CannotDisableModException e1) {
					errorMessage(selectedMod.getName() + " could not be disabled.");
				}
			}
		}
	}
	
	private class UpdateModAction extends AbstractAction {
		
		public UpdateModAction(){
			this("Update Mod");
		}
		
		protected UpdateModAction(String string){
			super(string);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (selectedMod != null){
				updateMod(selectedMod);
			}
		}
		
		protected void updateMod(Mod mod) {
			try {
				mpd.updateMod(selectedMod);
			} catch (ModUpdateFailedException e1) {
				errorMessage("There was an error updating " + mod.getName());
				e1.printStackTrace();
			}
		}
	}
	
	private class UpdateAllAction extends UpdateModAction {
		
		public UpdateAllAction() {
			super("Update All");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			for (Mod mod : sm.getMods()){
				updateMod(mod);
			}
		}
	}
	
	private class CheckforUpdatesAction extends AbstractAction {
		
		public CheckforUpdatesAction(){
			super("Check for Updates");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				mpd.checkForUpdates(mm, sm.getMods());
			} catch (ModUpdateFailedException e1) {
				errorMessage("Error checking for updates.");
			}
		}
	}
	
	private class LaunchKspAction extends AbstractAction {
		
		public LaunchKspAction(){
			super("Launch KSP");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				Runtime.getRuntime().exec(new Config().getKerbalExePath().toString());
			} catch (IOException e1) {
				errorMessage("Could not launch KSP!\n" + e1.getMessage());
				e1.printStackTrace();
			}
		}
		
	}
}
