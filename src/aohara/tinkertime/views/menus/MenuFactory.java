package aohara.tinkertime.views.menus;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import aohara.tinkertime.controllers.ModManager;

public class MenuFactory {
	
	public static JToolBar createToolBar(ModManager mm){
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		
		toolBar.add(new Actions.LaunchKspAction(toolBar, mm));
		
		toolBar.addSeparator();
		
		toolBar.add(new Actions.OptionsAction(toolBar, mm));
		
		toolBar.addSeparator();
		
		toolBar.add(new Actions.AddModAction(toolBar, mm));
		toolBar.add(new Actions.AddModZip(toolBar, mm));
		toolBar.add(new Actions.DeleteModAction(toolBar, mm));
		
		toolBar.addSeparator();
		
		toolBar.add(new Actions.EnableDisableModAction(toolBar, mm));

		toolBar.addSeparator();
		
		toolBar.add(new Actions.UpdateModAction(toolBar, mm));
		toolBar.add(new Actions.CheckforUpdatesAction(toolBar, mm));

		toolBar.addSeparator();
		
		toolBar.add(new Actions.HelpAction(toolBar, mm));
		
		return toolBar;
	}
	
	public static JMenuBar creatMenuBar(ModManager mm){
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new JMenuItem(new Actions.LaunchKspAction(menuBar, mm)));
		fileMenu.add(new JMenuItem(new Actions.OptionsAction(menuBar, mm)));
		fileMenu.add(new JMenuItem(new Actions.ExitAction(menuBar, mm)));
		menuBar.add(fileMenu);
		
		JMenu modMenu = new JMenu("Mod");
		modMenu.add(new JMenuItem(new Actions.AddModAction(menuBar, mm)));
		modMenu.add(new JMenuItem(new Actions.AddModZip(menuBar, mm)));
		modMenu.add(new JMenuItem(new Actions.EnableDisableModAction(menuBar, mm)));
		modMenu.add(new JMenuItem(new Actions.DeleteModAction(menuBar, mm)));
		modMenu.add(new JMenuItem(new Actions.UpdateModAction(menuBar, mm)));
		menuBar.add(modMenu);
		
		JMenu updateMenu = new JMenu("Updates");
		updateMenu.add(new JMenuItem(new Actions.UpdateAllAction(menuBar, mm)));
		updateMenu.add(new JMenuItem(new Actions.CheckforUpdatesAction(menuBar, mm)));
		updateMenu.add(new JMenuItem(new Actions.UpdateTinkerTime(menuBar, mm)));
		menuBar.add(updateMenu);
		
		JMenu importExportMenu = new JMenu("Import/Export Mods");
		importExportMenu.add(new JMenuItem(new Actions.ExportMods(menuBar, mm)));
		importExportMenu.add(new JMenuItem(new Actions.ImportMods(menuBar, mm)));
		menuBar.add(importExportMenu);
		
		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(new JMenuItem(new Actions.AboutAction(menuBar, mm)));
		helpMenu.add(new JMenuItem(new Actions.HelpAction(menuBar, mm)));
		helpMenu.add(new JMenuItem(new Actions.ContactAction(menuBar, mm)));
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
