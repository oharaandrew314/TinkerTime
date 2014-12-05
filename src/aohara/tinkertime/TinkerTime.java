package aohara.tinkertime;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import aohara.common.selectorPanel.SelectorPanel;
import aohara.common.workflows.ProgressPanel;
import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.controllers.ModStateManager;
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
		TinkerConfig config = TinkerConfig.create();
		
		ProgressPanel pp = new ProgressPanel();
		
		// Initialize Controllers
		ModStateManager sm = new ModStateManager(config);
		ModManager mm = ModManager.createDefaultModManager(config, sm, pp);
		
		// Set HTTP User-agent
		System.setProperty("http.agent", "TinkerTime Bot");
		
		// Initialize GUI
		SelectorPanel<Mod> sp = new SelectorPanel<Mod>(new ModView(config), new ModComparator(), new java.awt.Dimension(500, 600), 0.4f);
		sp.addControlPanel(true, new ModImageView(config));
		sp.addPopupMenu(MenuFactory.createPopupMenu(mm));
		sp.setListCellRenderer(new ModListCellRenderer(config));
		
		// Add Listeners
		sp.addListener(mm);
		sm.addListener(sp);

		// Start Application
		sm.getMods();  // Load mods (will notify selector panel)
		try {			
			// Check for Mod Updates
			if (config.autoCheckForModUpdates()){
				mm.checkForModUpdates();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		// Initialize Frame
		JFrame frame = new TinkerFrame();
		frame.setJMenuBar(MenuFactory.creatMenuBar(mm));
		frame.add(MenuFactory.createToolBar(mm), BorderLayout.NORTH);
		frame.add(sp.getComponent(), BorderLayout.CENTER);
		frame.add(pp.getComponent(), BorderLayout.SOUTH);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
