package aohara.tinkertime;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import aohara.common.selectorPanel.SelectorPanel;
import aohara.common.workflows.ProgressPanel;
import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.controllers.fileUpdater.ModuleManagerUpdateController;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModComparator;
import aohara.tinkertime.views.TinkerFrame;
import aohara.tinkertime.views.ModImageView;
import aohara.tinkertime.views.ModListCellRenderer;
import aohara.tinkertime.views.ModView;
import aohara.tinkertime.views.menus.MenuFactory;

/**
 * Main Class for Tinker Time
 * 
 * @author Andrew O'Hara
 */
public class TinkerTime {
	
	public static final String
		NAME = "Tinker Time",
		VERSION = "1.0",
		AUTHOR = "Andrew O'Hara";
	
	public static void main(String[] args) {		
		// Load and Verify Configuration
		Config config = new Config();
		config.verifyConfig();
		
		ProgressPanel pp = new ProgressPanel();
		
		// Initialize Controllers
		ModStateManager sm = new ModStateManager(config);
		ModManager mm = ModManager.createDefaultModManager(config, sm, pp);
		
		// Initialize GUI
		SelectorPanel<Mod> sp = new SelectorPanel<Mod>(new ModView(), new ModComparator());
		sp.addControlPanel(true, new ModImageView(config));
		sp.addPopupMenu(MenuFactory.createPopupMenu(mm));
		sp.setListCellRenderer(new ModListCellRenderer());
		
		// Add Listeners
		sp.addListener(mm);
		sm.addListener(sp);
		
		// Initialize Frame
		JFrame frame = new TinkerFrame();
		frame.setJMenuBar(MenuFactory.creatMenuBar(mm));
		frame.add(MenuFactory.createToolBar(mm), BorderLayout.NORTH);
		frame.add(sp.getComponent(), BorderLayout.CENTER);
		frame.add(pp.getComponent(), BorderLayout.SOUTH);
		frame.pack();
		frame.setVisible(true);

		// Start Application
		sm.getMods();  // Load mods (will notify selector panel)
		try {
			// Check for ModuleManager Update
			if (config.autoUpdateModuleManager()){
				new ModuleManagerUpdateController(mm, config).downloadUpdate(true);
			}
			
			// Check for Mod Updates
			if (config.autoCheckForModUpdates()){
				mm.checkForModUpdates();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}