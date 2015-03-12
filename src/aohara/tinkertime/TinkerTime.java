package aohara.tinkertime;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.github.zafarkhaja.semver.Version;

import aohara.common.selectorPanel.SelectorPanel;
import aohara.common.workflows.ProgressPanel;
import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.controllers.ModLoader;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
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
		AUTHOR = "Andrew O'Hara";
	public static final Version VERSION = Version.valueOf("1.1.1");
	public static final String FULL_NAME = String.format("%s v%s", NAME, VERSION);
	
	public static void main(String[] args) {
		TinkerConfig config = TinkerConfig.create();
		
		ProgressPanel pp = new ProgressPanel();
		
		// Initialize Controllers
		ModLoader modLoader = ModLoader.create(config);
		ModManager mm = ModManager.createDefaultModManager(config, modLoader, pp);
		
		// Set HTTP User-agent
		System.setProperty("http.agent", "TinkerTime Bot");
		
		// Initialize GUI
		SelectorPanel<Mod> sp = new SelectorPanel<Mod>(new ModView(config), new ModComparator(), new java.awt.Dimension(500, 600), 0.4f);
		sp.addControlPanel(true, new ModImageView(config));
		sp.addPopupMenu(MenuFactory.createPopupMenu(mm));
		sp.setListCellRenderer(new ModListCellRenderer(config));
		
		// Add Listeners
		sp.addListener(mm);
		modLoader.addListener(sp);

		// Start Application
		modLoader.init(mm);  // Load mods (will notify selector panel)
		
		// Check for App update on Startup
		if (config.isCheckForMMUpdatesOnStartup()){
			try {
				mm.tryUpdateModManager();
			} catch (UnsupportedHostException e) {
				JOptionPane.showMessageDialog(null, e.toString(), "Error Checking for App Updates", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		// Check for Mod Updates on Startup
		try {			
			if (config.autoCheckForModUpdates()){
				mm.checkForModUpdates();
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.toString(), "Error Checking for Mod Updates", JOptionPane.ERROR_MESSAGE);
		}
		
		// Initialize Frame
		JFrame frame = new TinkerFrame();
		frame.setJMenuBar(MenuFactory.createMenuBar(mm));
		frame.add(MenuFactory.createToolBar(mm), BorderLayout.NORTH);
		frame.add(sp.getComponent(), BorderLayout.CENTER);
		frame.add(pp.getComponent(), BorderLayout.SOUTH);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
