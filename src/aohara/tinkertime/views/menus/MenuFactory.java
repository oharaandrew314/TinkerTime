package aohara.tinkertime.views.menus;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.io.kspLauncher.GameLauncher;
import aohara.tinkertime.models.ConfigFactory;
import aohara.tinkertime.views.InstallationSelector;

import com.google.inject.Inject;

public class MenuFactory {

	private final ModManager mm;
	private final ConfigFactory configFactory;
	private final GameLauncher gameLauncher;
	private final InstallationSelector installationSelector;

	@Inject
	MenuFactory(ModManager mm, ConfigFactory configFactory, GameLauncher gameLauncher, InstallationSelector installationSelector){
		this.mm = mm;
		this.configFactory = configFactory;
		this.gameLauncher = gameLauncher;
		this.installationSelector = installationSelector;
	}

	public JToolBar createToolBar(){
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);

		toolBar.add(new Actions.LaunchKspAction(toolBar, mm, gameLauncher)).setFocusPainted(false);
		toolBar.addSeparator();

		toolBar.add(new Actions.OpenGameDataFolder(toolBar, mm, configFactory)).setFocusPainted(false);
		toolBar.add(new Actions.LaunchInstallationSelector(toolBar, mm, installationSelector)).setFocusPainted(false);
		//toolBar.add(new Actions.OptionsAction(toolBar, configController)).setFocusPainted(false); FIXME reimplement options toolbar button

		toolBar.addSeparator();

		toolBar.add(new Actions.AddModAction(toolBar, mm)).setFocusPainted(false);
		toolBar.add(new Actions.AddModZip(toolBar, mm)).setFocusPainted(false);

		toolBar.addSeparator();

		toolBar.add(new Actions.UpdateModAction(toolBar, mm)).setFocusPainted(false);
		toolBar.add(new Actions.CheckforUpdatesAction(toolBar, mm)).setFocusPainted(false);

		return toolBar;
	}

	public JMenuBar createMenuBar(){
		JMenuBar menuBar = new JMenuBar();

		JMenu modMenu = new JMenu("Mod");
		modMenu.add(new Actions.EnableDisableModAction(menuBar, mm).withoutIcon());
		modMenu.add(new Actions.UpdateModAction(menuBar, mm).withoutIcon());
		modMenu.add(new Actions.DeleteModAction(menuBar, mm).withoutIcon());
		menuBar.add(modMenu);

		JMenu updateMenu = new JMenu("Updates");
		updateMenu.add(new Actions.UpdateAllAction(menuBar, mm).withoutIcon());
		updateMenu.add(new Actions.CheckforUpdatesAction(menuBar, mm).withoutIcon());
		updateMenu.add(new Actions.UpdateTinkerTime(menuBar, mm).withoutIcon());
		menuBar.add(updateMenu);

		//TODO Update to new Version
		//JMenu importExportMenu = new JMenu("Import/Export");
		//importExportMenu.add(new Actions.ExportMods(menuBar, mm).withoutIcon());
		//importExportMenu.add(new Actions.ImportMods(menuBar, mm).withoutIcon());
		//menuBar.add(importExportMenu);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(new Actions.AboutAction(menuBar, mm).withoutIcon());
		helpMenu.add(Actions.newHelpAction(menuBar).withoutIcon());
		helpMenu.add(new Actions.ContactAction(menuBar, mm).withoutIcon());
		menuBar.add(helpMenu);

		return menuBar;
	}

	public JPopupMenu createPopupMenu(){
		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.add(new Actions.EnableDisableModAction(popupMenu, mm));
		popupMenu.add(new Actions.UpdateModAction(popupMenu, mm));
		popupMenu.add(new Actions.DeleteModAction(popupMenu, mm));
		return popupMenu;
	}

}
