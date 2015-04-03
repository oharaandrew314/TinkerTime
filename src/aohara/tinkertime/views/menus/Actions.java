package aohara.tinkertime.views.menus;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import aohara.common.Util;
import aohara.common.content.ImageManager;
import aohara.tinkertime.ModManager;
import aohara.tinkertime.ModManager.NoModSelectedException;
import aohara.tinkertime.TinkerTime;
import aohara.tinkertime.controllers.launcher.GameLauncher;
import aohara.tinkertime.crawlers.CrawlerFactory;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.views.Dialogs;
import aohara.tinkertime.views.FileChoosers;
import aohara.tinkertime.views.UrlPanel;

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
		
		public void actionPerformed(ActionEvent evt) {
			try {
				call();
			} catch (Exception e){
				e.printStackTrace();
				Dialogs.errorDialog(parent, e);
			}
		}
		
		protected abstract void call() throws Exception;
	}
	
	@SuppressWarnings("serial")
	private static final class GoToUrlAction extends TinkerAction {
		
		private final URL url;
		
		GoToUrlAction(String title, String url, String iconPath, JComponent parent) {
			super(title, iconPath, parent, null);
			try {
				this.url = new URL(url);
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		protected void call() throws Exception {
			Util.goToHyperlink(url);
		}
	}
	
	// -- Actions -----------------------------------------------------------
	
	@SuppressWarnings("serial")
	static class AddModAction extends TinkerAction {
		
		AddModAction(JComponent parent, ModManager mm){
			super("Add Mod", "icon/glyphicons_432_plus.png", parent, mm);
		}

		@Override
		protected void call() throws Exception {
			// Get URL from user
			String urlString = JOptionPane.showInputDialog(
				parent,
				"Please enter the URL of the mod you would like to"
				+ " add.\ne.g. http://www.curse.com/ksp-mods/kerbal/220221-mechjeb\n\n"
				+ "Supported Hosts are " + Arrays.asList(CrawlerFactory.ACCEPTED_MOD_HOSTS),
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
				mm.downloadMod(new URL("http://" + urlString));
			}
		}
	}
	
	@SuppressWarnings("serial")
	static class DeleteModAction extends TinkerAction {
		
		DeleteModAction(JComponent parent, ModManager mm){
			super("Delete Mod", "icon/glyphicons_433_minus.png", parent, mm);
		}

		@Override
		protected void call() throws Exception {
			try {
				Mod selectedMod = mm.getSelectedMod();
				
				if (Dialogs.confirmDeleteMod(parent, selectedMod.name)){
					mm.deleteMod(selectedMod);
				}
			} catch (NoModSelectedException ex){
				// Do Nothing
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
		protected void call() throws Exception {
			try {
				mm.updateMod(mm.getSelectedMod(), true);
			} catch (NoModSelectedException ex){
				// Do Nothing
			}
		}
	}
	
	@SuppressWarnings("serial")
	static class UpdateAllAction extends UpdateModAction {
		
		UpdateAllAction(JComponent parent, ModManager mm) {
			super("Update All", parent, mm);
		}

		@Override
		protected void call() throws Exception {
			mm.updateMods();
		}
	}
	
	@SuppressWarnings("serial")
	static class CheckforUpdatesAction extends TinkerAction {
		
		CheckforUpdatesAction(JComponent parent, ModManager mm){
			super("Check for Updates", "icon/glyphicons_027_search.png", parent, mm);
		}

		@Override
		protected void call() throws Exception {
			mm.checkForModUpdates();
		}
	}
	
	@SuppressWarnings("serial")
	static class EnableDisableModAction extends TinkerAction {
		
		EnableDisableModAction(JComponent parent, ModManager mm){
			super("Enable/Disable", "icon/glyphicons_457_transfer.png", parent, mm);
		}

		@Override
		protected void call() throws Exception {
			try {
				mm.toggleMod(mm.getSelectedMod());
			} catch (NoModSelectedException ex){
				// Do Nothing
			}
		}
	}
	
	@SuppressWarnings("serial")
	static class OptionsAction extends TinkerAction {
		
		OptionsAction(JComponent parent, ModManager mm){
			super("Options", "icon/glyphicons_439_wrench.png", parent, mm);
		}

		@Override
		protected void call() throws Exception {
			mm.openConfigWindow();
		}
	}
	
	@SuppressWarnings("serial")
	static class ExitAction extends TinkerAction {
		
		ExitAction(JComponent parent, ModManager mm){
			super("Exit", "icon/glyphicons_063_power.png", parent, mm);
		}

		@Override
		protected void call() throws Exception {
			System.exit(0);
		}
	}
	
	static TinkerAction newHelpAction(JComponent parent){
		return new GoToUrlAction(
			"Help",
			"https://github.com/oharaandrew314/TinkerTime/wiki",
			"icon/glyphicons_194_circle_question_mark.png",
			parent
		);
	}
	
	@SuppressWarnings("serial")
	static class AboutAction extends TinkerAction {
		
		AboutAction(JComponent parent, ModManager mm){
			super("About", "icon/glyphicons_003_user.png", parent, mm);
		}

		@Override
		protected void call() throws Exception {
			Object[] message = {
				TinkerTime.FULL_NAME,
				"\n",
				"This work is licensed under the Creative Commons \n" +
				"Attribution-ShareAlike 4.0 International License.\n",
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
		}
	}
	
	static TinkerAction newContactAction(JComponent parent){
		return new GoToUrlAction(
			"Contact Me",
			"http://tinkertime.uservoice.com",
			"icon/glyphicons_010_envelope.png",
			parent
		);
	}
	
	@SuppressWarnings("serial")
	static class ExportMods extends TinkerAction {
		
		ExportMods(JComponent parent, ModManager mm){
			super("Export Enabled Mods", "icon/glyphicons_359_file_export.png", parent, mm);
		}

		@Override
		protected void call() throws Exception {
			mm.exportEnabledMods(FileChoosers.chooseJsonFile(true));
			JOptionPane.showMessageDialog(
				parent,
				"Enabled mod data has been exported.",
				"Exported",
				JOptionPane.INFORMATION_MESSAGE
			);
		}
	}
	
	@SuppressWarnings("serial")
	static class ImportMods extends TinkerAction {
		
		ImportMods(JComponent parent, ModManager mm){
			super("Import Mods", "icon/glyphicons-359-file-import.png", parent, mm);
		}

		@Override
		protected void call() throws Exception {
			mm.importMods(FileChoosers.chooseJsonFile(false));
			JOptionPane.showMessageDialog(
				parent,
				"The Mods have been imported.",
				"Imported",
				JOptionPane.INFORMATION_MESSAGE
			);
		}
		
	}
	
	@SuppressWarnings("serial")
	static class UpdateTinkerTime extends TinkerAction {
		
		UpdateTinkerTime(JComponent parent, ModManager mm){
			super("Update Tinker Time", null, parent, mm);
		}

		@Override
		protected void call() throws Exception {
			mm.tryUpdateModManager();
		}
		
	}
	
	@SuppressWarnings("serial")
	static class AddModZip extends TinkerAction {
		
		AddModZip(JComponent parent, ModManager mm){
			super("Add Mod from Zip File", "icon/glyphicons_410_compressed.png", parent, mm);
		}

		@Override
		protected void call() throws Exception {
			try {
				mm.addModZip(FileChoosers.chooseModZip());
			} catch (FileNotFoundException e){
				// Do nothing if file was not chosen
			}
		}	
	}
	
	@SuppressWarnings("serial")
	static class LaunchKspAction extends TinkerAction {
		
		private final GameLauncher launcher;
		
		LaunchKspAction(JComponent parent, ModManager mm){
			super("Launch KSP", "icon/rocket.png", parent, mm);
			launcher = GameLauncher.create(mm.config);
		}

		@Override
		protected void call() throws Exception {
			launcher.launchGame();
		}
	}
	
	@SuppressWarnings("serial")
	static class OpenGameDataFolder extends TinkerAction {
		
		public OpenGameDataFolder(JComponent parent, ModManager mm) {
			super("Open GameData Folder", "icon/glyphicons_144_folder_open.png", parent, mm);
		}

		@Override
		protected void call() throws Exception {
			Desktop.getDesktop().open(mm.config.getGameDataPath().toFile());
		}
		
	}
}
