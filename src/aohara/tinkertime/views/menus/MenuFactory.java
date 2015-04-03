package aohara.tinkertime.views.menus;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import aohara.tinkertime.ModManager;

public class MenuFactory {
	
	public static JToolBar createToolBar(ModManager mm){
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		
		toolBar.add(new Actions.LaunchKspAction(toolBar, mm)).setFocusPainted(false);
		toolBar.add(new Actions.OpenGameDataFolder(toolBar, mm)).setFocusPainted(false);
		
		toolBar.addSeparator();
		
		toolBar.add(new Actions.OptionsAction(toolBar, mm)).setFocusPainted(false);
		
		toolBar.addSeparator();
		
		toolBar.add(new Actions.AddModAction(toolBar, mm)).setFocusPainted(false);
		toolBar.add(new Actions.AddModZip(toolBar, mm)).setFocusPainted(false);
		toolBar.add(new Actions.DeleteModAction(toolBar, mm)).setFocusPainted(false);
		
		toolBar.addSeparator();
		
		toolBar.add(new Actions.EnableDisableModAction(toolBar, mm)).setFocusPainted(false);

		toolBar.addSeparator();
		
		toolBar.add(new Actions.UpdateModAction(toolBar, mm)).setFocusPainted(false);
		toolBar.add(new Actions.CheckforUpdatesAction(toolBar, mm)).setFocusPainted(false);

		toolBar.addSeparator();
		
		toolBar.add(Actions.newHelpAction(toolBar)).setFocusPainted(false);
		
		return toolBar;
	}
	
	public static JMenuBar createMenuBar(ModManager mm){
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new Actions.LaunchKspAction(menuBar, mm));
		fileMenu.add(new Actions.OpenGameDataFolder(menuBar, mm));
		fileMenu.add(new Actions.OptionsAction(menuBar, mm));
		fileMenu.add(new Actions.ExitAction(menuBar, mm));
		menuBar.add(fileMenu);
		
		JMenu modMenu = new JMenu("Mod");
		modMenu.add(new Actions.AddModAction(menuBar, mm));
		modMenu.add(new Actions.AddModZip(menuBar, mm));
		modMenu.add(new Actions.EnableDisableModAction(menuBar, mm));
		modMenu.add(new Actions.DeleteModAction(menuBar, mm));
		modMenu.add(new Actions.UpdateModAction(menuBar, mm));
		menuBar.add(modMenu);
		
		JMenu updateMenu = new JMenu("Updates");
		updateMenu.add(new Actions.UpdateAllAction(menuBar, mm));
		updateMenu.add(new Actions.CheckforUpdatesAction(menuBar, mm));
		updateMenu.add(new Actions.UpdateTinkerTime(menuBar, mm));
		menuBar.add(updateMenu);
		
		JMenu importExportMenu = new JMenu("Import/Export Mods");
		importExportMenu.add(new Actions.ExportMods(menuBar, mm));
		importExportMenu.add(new Actions.ImportMods(menuBar, mm));
		menuBar.add(importExportMenu);
		
		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(new Actions.AboutAction(menuBar, mm));
		helpMenu.add(Actions.newHelpAction(menuBar));
		helpMenu.add(Actions.newContactAction(menuBar));
		menuBar.add(helpMenu);
		
		return menuBar;
	}
	
	public static JPopupMenu createPopupMenu(ModManager mm){
		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.add(new Actions.EnableDisableModAction(popupMenu, mm));
		popupMenu.add(new Actions.UpdateModAction(popupMenu, mm));
		popupMenu.add(new Actions.DeleteModAction(popupMenu, mm));
		return popupMenu;
	}

}
