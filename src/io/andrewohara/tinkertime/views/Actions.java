package io.andrewohara.tinkertime.views;

import io.andrewohara.common.Util;
import io.andrewohara.common.content.ImageManager;
import io.andrewohara.common.views.Dialogs;
import io.andrewohara.common.views.UrlLabels;
import io.andrewohara.tinkertime.TinkerTimeLauncher;
import io.andrewohara.tinkertime.controllers.ImportController;
import io.andrewohara.tinkertime.controllers.ModExceptions.NoModSelectedException;
import io.andrewohara.tinkertime.controllers.ModManager;
import io.andrewohara.tinkertime.db.ConfigFactory;
import io.andrewohara.tinkertime.io.crawlers.CrawlerFactory;
import io.andrewohara.tinkertime.io.kspLauncher.GameLauncher;
import io.andrewohara.tinkertime.models.ConfigData;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

public class Actions {

	// -- Helpers ---------------------------------------------------------

	@SuppressWarnings("serial")
	public
	static abstract class TinkerAction extends AbstractAction {

		private static final ImageManager IMAGE_MANAGER = new ImageManager();;
		protected final JComponent parent;
		protected final ModManager mm;

		private TinkerAction(String title, String iconName, JComponent parent, ModManager mm){
			super(title, iconName != null ? IMAGE_MANAGER.getIcon(iconName): null);
			this.parent = parent;
			this.mm = mm;
			putValue(Action.SHORT_DESCRIPTION, title);
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			try {
				call();
			} catch (Exception e){
				Dialogs.errorDialog(parent, e);
			}
		}

		public TinkerAction withoutIcon(){
			putValue(Action.SMALL_ICON, null);
			return this;
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
	public static class AddModAction extends TinkerAction {

		public AddModAction(JComponent parent, ModManager mm){
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
				mm.downloadNewMod(new URL(urlString));
			} catch(MalformedURLException ex){
				mm.downloadNewMod(new URL("http://" + urlString));
			}
		}
	}

	@SuppressWarnings("serial")
	public static class DeleteModAction extends TinkerAction {

		public DeleteModAction(JComponent parent, ModManager mm){
			super("Delete Mod", "icon/glyphicons_433_minus.png", parent, mm);
		}

		@Override
		protected void call() throws Exception {
			try {
				Mod selectedMod = mm.getSelectedMod();

				if (TinkerDialogs.confirmDeleteMod(parent, selectedMod.getName())){
					mm.deleteMod(selectedMod);
				}
			} catch (NoModSelectedException ex){
				// Do Nothing
			}
		}
	}

	@SuppressWarnings("serial")
	public static class UpdateModAction extends TinkerAction {

		public UpdateModAction(JComponent parent, ModManager mm){
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
	public static class UpdateAllAction extends UpdateModAction {

		public UpdateAllAction(JComponent parent, ModManager mm) {
			super("Update All", parent, mm);
		}

		@Override
		protected void call() throws Exception {
			mm.updateMods();
		}
	}

	@SuppressWarnings("serial")
	public static class CheckforUpdatesAction extends TinkerAction {

		public CheckforUpdatesAction(JComponent parent, ModManager mm){
			super("Check for Mod Updates", "icon/glyphicons_027_search.png", parent, mm);
		}

		@Override
		protected void call() throws Exception {
			mm.checkForModUpdates();
		}
	}

	@SuppressWarnings("serial")
	public static class EnableDisableModAction extends TinkerAction {

		public EnableDisableModAction(JComponent parent, ModManager mm){
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

	public static TinkerAction newHelpAction(JComponent parent){
		return new GoToUrlAction(
				"Help",
				"https://github.com/oharaandrew314/TinkerTime/wiki",
				"icon/glyphicons_194_circle_question_mark.png",
				parent
				);
	}

	@SuppressWarnings("serial")
	public static class AboutAction extends TinkerAction {

		public AboutAction(JComponent parent, ModManager mm){
			super("About", "icon/glyphicons_003_user.png", parent, mm);
		}

		@Override
		protected void call() throws Exception {
			Object[] message = {
					TinkerTimeLauncher.FULL_NAME,
					"\n",
					"This work is licensed under the Creative Commons \n" +
							"Attribution-ShareAlike 4.0 International License.\n",
							new UrlLabels.UrlLink("View a copy of this license", new URL("http://creativecommons.org/licenses/by-sa/4.0/")).getComponent(),
							"\n",
							TinkerTimeLauncher.NAME + " uses Glyphicons (glyphicons.com)"
			};
			JOptionPane.showMessageDialog(
					parent,
					message,
					"About " + TinkerTimeLauncher.NAME,
					JOptionPane.INFORMATION_MESSAGE
					);
		}
	}

	@SuppressWarnings("serial")
	public static class ContactAction extends TinkerAction {

		public ContactAction(JComponent parent, ModManager mm){
			super("Contact Me", "icon/glyphicons_010_envelope.png", parent, mm);
		}

		@Override
		protected void call() throws Exception {
			Desktop desktop = Desktop.getDesktop();
			String message = "mailto:tinkertime@andrewohara.io?subject=TinkerTime%20Support%20Request";
			URI uri = URI.create(message);
			desktop.mail(uri);
		}

	}

	@SuppressWarnings("serial")
	public static class ExportMods extends TinkerAction {

		private final ImportController importController;

		public ExportMods(JComponent parent, ImportController importController){
			super("Export Mods", "icon/glyphicons_359_file_export.png", parent, null);
			this.importController = importController;
		}

		@Override
		protected void call() throws Exception {
			int result = JOptionPane.showConfirmDialog(parent, "Would you like to export only the enabled mods?  Otherwise, all.", "Choose mods to export", JOptionPane.YES_NO_CANCEL_OPTION);
			boolean enabledOnly = false;
			switch(result){
			case JOptionPane.YES_OPTION: enabledOnly = true; break;
			case JOptionPane.NO_OPTION: enabledOnly = false; break;
			case JOptionPane.CANCEL_OPTION:
			default: return;
			}

			Path exportPath = FileChoosers.chooseImportExportFile(true);
			int exported = importController.exportMods(exportPath, enabledOnly);
			JOptionPane.showMessageDialog(
					parent,
					String.format("%d Mods have been exported.", exported),
					"Exported",
					JOptionPane.INFORMATION_MESSAGE
					);
		}
	}

	@SuppressWarnings("serial")
	public static class ImportMods extends TinkerAction {

		private final ImportController importController;

		public ImportMods(JComponent parent, ImportController importController){
			super("Import Mods", "icon/glyphicons-359-file-import.png", parent, null);
			this.importController = importController;
		}

		@Override
		protected void call() throws Exception {
			int imported = importController.importMods(FileChoosers.chooseImportExportFile(false));
			JOptionPane.showMessageDialog(
					parent,
					String.format("%d Mod(s) have been imported.", imported),
					"Imported",
					JOptionPane.INFORMATION_MESSAGE
					);
		}
	}

	@SuppressWarnings("serial")
	public static class UpdateTinkerTime extends TinkerAction {

		public UpdateTinkerTime(JComponent parent, ModManager mm){
			super("Check for Tinker Time Update", null, parent, mm);
		}

		@Override
		protected void call() throws Exception {
			mm.tryUpdateModManager();
		}

	}

	@SuppressWarnings("serial")
	public static class AddModZip extends TinkerAction {

		public AddModZip(JComponent parent, ModManager mm){
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
	public static class LaunchKspAction extends TinkerAction {

		private final GameLauncher launcher;

		public LaunchKspAction(JComponent parent, ModManager mm, GameLauncher launcher){
			super("Launch KSP", "icon/rocket.png", parent, mm);
			this.launcher = launcher;
		}

		@Override
		protected void call() throws Exception {
			launcher.launchGame();
		}
	}

	@SuppressWarnings("serial")
	public static class OpenGameDataFolder extends TinkerAction {

		private final ConfigFactory configFactory;

		public OpenGameDataFolder(JComponent parent, ModManager mm, ConfigFactory configFactory) {
			super("Open GameData Folder", "icon/glyphicons_144_folder_open.png", parent, mm);
			this.configFactory = configFactory;
		}

		@Override
		protected void call() throws Exception {
			Desktop.getDesktop().open(configFactory.getConfig().getSelectedInstallation().getGameDataPath().toFile());
		}
	}

	public static class LaunchInstallationSelector extends TinkerAction {

		private final InstallationSelectorView selector;

		public LaunchInstallationSelector(JComponent parent, ModManager mm, InstallationSelectorView selector) {
			super("Select KSP Installation", "icon/glyphicons_342_hdd.png", parent, mm);
			this.selector = selector;
		}

		@Override
		protected void call() throws Exception {
			selector.toDialog();
		}
	}

	//////////////////////////////
	// Options Action Listeners //
	//////////////////////////////

	public static class CheckForAppUpdatesAction implements ActionListener {

		private final JCheckBox checkBox;
		private final ConfigFactory configFactory;

		public CheckForAppUpdatesAction(JCheckBox checkBox, ConfigFactory configFactory){
			this.checkBox = checkBox;
			this.configFactory = configFactory;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			ConfigData config = configFactory.getConfig();
			config.setCheckForAppUpdatesOnStartup(checkBox.isSelected());
			configFactory.update(config);
		}
	}

	public static class CheckForModUpdatesAction implements ActionListener {

		private final JCheckBox checkBox;
		private final ConfigFactory configFactory;

		public CheckForModUpdatesAction(JCheckBox checkBox, ConfigFactory configFactory){
			this.checkBox = checkBox;
			this.configFactory = configFactory;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			ConfigData config = configFactory.getConfig();
			config.setCheckForModUpdatesOnStartup(checkBox.isSelected());
			configFactory.update(config);
		}
	}
}
