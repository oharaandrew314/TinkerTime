package aohara.tinkertime.views.menus;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import aohara.common.Util;
import aohara.common.content.ImageManager;
import aohara.common.workflows.tasks.BrowserGoToTask;
import aohara.tinkertime.TinkerTime;
import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.controllers.ModManager.CannotDisableModException;
import aohara.tinkertime.controllers.ModManager.ModAlreadyDisabledException;
import aohara.tinkertime.controllers.ModManager.ModAlreadyEnabledException;
import aohara.tinkertime.controllers.ModManager.ModNotDownloadedException;
import aohara.tinkertime.controllers.ModManager.ModUpdateFailedException;
import aohara.tinkertime.crawlers.Constants;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.models.DefaultMods;
import aohara.tinkertime.models.FileUpdateListener;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.views.UrlPanel;
import aohara.tinkertime.workflows.ModWorkflowBuilder;

class Actions {
	
	// -- Helpers ---------------------------------------------------------
	
	@SuppressWarnings("serial")
	private static abstract class TinkerAction extends AbstractAction {
		
		private static final ImageManager IMAGE_MANAGER = new ImageManager();;
		protected final JComponent parent;
		protected final ModManager mm;
		
		private TinkerAction(String title, String iconName, JComponent parent, ModManager mm){
			super(title, iconName != null ? IMAGE_MANAGER.getIcon(iconName): null);
			this.parent = parent;
			this.mm = mm;
			putValue(Action.SHORT_DESCRIPTION, title);
		}
		
		protected void errorMessage(Exception ex){
			ex.printStackTrace();
			errorMessage(ex.toString());
		}
		
		protected void errorMessage(String message){
			JOptionPane.showMessageDialog(
				null, message, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	// -- Actions -----------------------------------------------------------
	
	@SuppressWarnings("serial")
	static class AddModAction extends TinkerAction {
		
		AddModAction(JComponent parent, ModManager mm){
			super("Add Mod", "icon/glyphicons_432_plus.png", parent, mm);
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			// Get URL from user
			String urlString = JOptionPane.showInputDialog(
				parent,
				"Please enter the URL of the mod you would like to"
				+ " add.\ne.g. http://www.curse.com/ksp-mods/kerbal/220221-mechjeb\n\n"
				+ "Supported Hosts are " + Arrays.asList(Constants.ACCEPTED_MOD_HOSTS),
				"Enter Mod Page URL",
				JOptionPane.QUESTION_MESSAGE
			);
			
			if (urlString == null || urlString.trim().isEmpty()){
				return;
			}
			
			// Try to add Mod
			try {
				mm.downloadMod(new URL(urlString));
			} catch(MalformedURLException ex){
				try {
					mm.downloadMod(new URL("http://" + urlString));
				} catch (MalformedURLException | ModUpdateFailedException| UnsupportedHostException e) {
					errorMessage(ex);
				}
			} catch (UnsupportedHostException | ModUpdateFailedException ex) {
				errorMessage(ex);
			}
		}
	}
	
	@SuppressWarnings("serial")
	static class DeleteModAction extends TinkerAction {
		
		DeleteModAction(JComponent parent, ModManager mm){
			super("Delete Mod", "icon/glyphicons_433_minus.png", parent, mm);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Mod selectedMod = mm.getSelectedMod();
			if (selectedMod != null){
				if (DefaultMods.isBuiltIn(selectedMod)){
					errorMessage("Cannot delete built-in mod: " + selectedMod.getName());
					return;
				}
				
				try {
					if (JOptionPane.showConfirmDialog(
						parent,
						"Are you sure you want to delete "
						+ selectedMod.getName() + "?",
						"Delete?",
						JOptionPane.YES_NO_OPTION
					) == JOptionPane.YES_OPTION){
						mm.deleteMod(selectedMod);
					}
				} catch (CannotDisableModException | IOException e1) {
					errorMessage(selectedMod.getName() + " could not be disabled.");
				}
			}
		}
	}
	
	@SuppressWarnings("serial")
	static class UpdateModAction extends TinkerAction {
		
		UpdateModAction(JComponent parent, ModManager mm){
			this("Update Mod", parent, mm);
		}
		
		private UpdateModAction(String title, JComponent parent, ModManager mm){
			super(title, "icon/glyphicons_181_download_alt.png", parent, mm);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (mm.getSelectedMod() != null){
				try {
					mm.updateMod(mm.getSelectedMod());
				} catch (ModUpdateFailedException e1) {
					errorMessage(e1);
				}
			}
		}
	}
	
	@SuppressWarnings("serial")
	static class UpdateAllAction extends UpdateModAction {
		
		UpdateAllAction(JComponent parent, ModManager mm) {
			super("Update All", parent, mm);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				mm.updateMods();
			} catch (ModUpdateFailedException e1) {
				errorMessage("One or more mods failed to update");
			}
		}
	}
	
	@SuppressWarnings("serial")
	static class CheckforUpdatesAction extends TinkerAction {
		
		CheckforUpdatesAction(JComponent parent, ModManager mm){
			super("Check for Updates", "icon/glyphicons_027_search.png", parent, mm);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				mm.checkForModUpdates();
			} catch (Exception e1) {
				e1.printStackTrace();
				errorMessage("Error checking for updates.");
			}
		}
	}
	
	@SuppressWarnings("serial")
	static class EnableDisableModAction extends TinkerAction {
		
		EnableDisableModAction(JComponent parent, ModManager mm){
			super("Enable/Disable", "icon/glyphicons_457_transfer.png", parent, mm);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Mod selectedMod = mm.getSelectedMod();
			if (selectedMod != null && selectedMod.isEnabled()){
				try {
					mm.disableMod(selectedMod);
				} catch (ModAlreadyDisabledException | IOException e1) {
					errorMessage(e1);
				}
			} else if (selectedMod != null){
				try {
					mm.enableMod(selectedMod);
				} catch (ModAlreadyEnabledException | ModNotDownloadedException | IOException e1) {
					errorMessage(e1);
				}
			}
		}
	}
	
	@SuppressWarnings("serial")
	static class OptionsAction extends TinkerAction {
		
		OptionsAction(JComponent parent, ModManager mm){
			super("Options", "icon/glyphicons_439_wrench.png", parent, mm);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			mm.config.updateConfig(true, false);
		}
	}
	
	@SuppressWarnings("serial")
	static class ExitAction extends TinkerAction {
		
		ExitAction(JComponent parent, ModManager mm){
			super("Exit", "icon/glyphicons_063_power.png", parent, mm);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}
	
	@SuppressWarnings("serial")
	static class HelpAction extends TinkerAction {
		
		HelpAction(JComponent parent, ModManager mm){
			super("Help", "icon/glyphicons_194_circle_question_mark.png", parent, mm);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				Util.goToHyperlink(new URL("https://github.com/oharaandrew314/TinkerTime/wiki"));
			} catch (IOException e1) {
				errorMessage("Error opening help");
			}
		}
	}
	
	@SuppressWarnings("serial")
	static class AboutAction extends TinkerAction {
		
		AboutAction(JComponent parent, ModManager mm){
			super("About", "icon/glyphicons_003_user.png", parent, mm);
		}

		@Override
		public void actionPerformed(ActionEvent e) {			
			try {
				Object[] message = {
					String.format(
						"<html>%s v%s - by %s\n",
						TinkerTime.NAME,
						TinkerTime.VERSION,
						TinkerTime.AUTHOR
					),
					"\n",
					"This work is licensed under the Creative Commons \n"
							+ "Attribution-ShareAlike 4.0 International License.\n",
					new UrlPanel("View a copy of this license", new URL("http://creativecommons.org/licenses/by-sa/4.0/")).getComponent(),
					"\n",
					TinkerTime.NAME + " uses Glyphicons (glyphicons.com)"
				};
				JOptionPane.showMessageDialog(
						parent,
						message,
						"About " + TinkerTime.NAME,
						JOptionPane.INFORMATION_MESSAGE
					);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("serial")
	static class ContactAction extends TinkerAction {
		
		ContactAction(JComponent parent, ModManager mm){
			super("Contact Me", "icon/glyphicons_010_envelope.png", parent, mm);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				Util.goToHyperlink(new URL("http://tinkertime.uservoice.com"));
			} catch (IOException e1) {
				errorMessage(e1.getMessage());
			}
		}
	}
	
	@SuppressWarnings("serial")
	static class ExportModList extends TinkerAction {
		
		ExportModList(JComponent parent, ModManager mm){
			super("Export Enabled Mod Data", "icon/glyphicons_359_file_export.png", parent, mm);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Choose a location to save the mod data");
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);  // Only accept files
			chooser.setFileFilter(new FileNameExtensionFilter("Json File", "json"));  // Only accept JSON files
			chooser.setSelectedFile(new java.io.File("mods.json"));
			if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION){
				mm.exportEnabledMods(chooser.getSelectedFile().toPath());
				JOptionPane.showMessageDialog(
					parent,
					"Enabled mod data has been exported",
					"Exported",
					JOptionPane.INFORMATION_MESSAGE
				);
			}
		}
	}
	
	@SuppressWarnings("serial")
	static class UpdateTinkerTime extends TinkerAction implements FileUpdateListener {
		
		UpdateTinkerTime(JComponent parent, ModManager mm){
			super("Update Tinker Time", null, parent, mm);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			ModWorkflowBuilder builder = new ModWorkflowBuilder("Updating " + TinkerTime.NAME);
			try {
				builder.checkForUpdates(Constants.getTinkerTimeGithubUrl(), null, TinkerTime.NAME, this);
				mm.submitDownloadWorkflow(builder.buildWorkflow());
			} catch (UnsupportedHostException ex) {
				errorMessage(ex);
			}
		}

		@Override
		public void setUpdateAvailable(URL pageUrl, URL downloadLink, String newestFileName) {
			if (JOptionPane.showConfirmDialog(
				parent, String.format("Current: %s\nAvailable: %s\nDownload?", TinkerTime.VERSION, newestFileName), "Update Tinker Time", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
			) == JOptionPane.YES_OPTION){
				try {
					new BrowserGoToTask(downloadLink).call(null);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
	}
	
	@SuppressWarnings("serial")
	static class AddModZip extends TinkerAction {
		
		AddModZip(JComponent parent, ModManager mm){
			super("Add Mod from Zip File", "icon/glyphicons_410_compressed.png", parent, mm);
		}

		@Override
		public void actionPerformed(ActionEvent e) {			
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Please select the mod zip to add.");
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);  // Only accept files
			chooser.setFileFilter(new FileNameExtensionFilter("Zip Archive", "zip"));
			if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION){
				mm.addModZip(chooser.getSelectedFile().toPath());
			}
		}
		
	}
}
