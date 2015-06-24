package aohara.tinkertime;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.net.MalformedURLException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import aohara.common.version.Version;
import aohara.common.views.selectorPanel.SelectorPanelController;
import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.modules.MainModule;
import aohara.tinkertime.resources.Icons;
import aohara.tinkertime.resources.ModMetaLoader;
import aohara.tinkertime.views.ModListCellRenderer;
import aohara.tinkertime.views.factories.ModSelectorPanelFactory;
import aohara.tinkertime.views.menus.MenuFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Main Class for Tinker Time
 * 
 * @author Andrew O'Hara
 */
public class TinkerTime {
	
	public static final String
		NAME = "Tinker Time",
		AUTHOR = "oharaandrew314",
		DOWNLOAD_URL = "https://kerbalstuff.com/mod/243";
	public static final Version VERSION = Version.valueOf("1.4.2");
	public static final String
		SAFE_NAME = NAME.replace(" ", ""),
		FULL_NAME = String.format("%s v%s by %s", NAME, VERSION, AUTHOR);
	
	public static void main(String[] args) {
		// Set HTTP User-agent
		System.setProperty("http.agent", "TinkerTime Bot");
		
		Injector injector = Guice.createInjector(new MainModule());
		
		// Initialize Controllers
		ModMetaLoader modLoader = injector.getInstance(ModMetaLoader.class);
		ModManager modManager = injector.getInstance(ModManager.class);
		
		// Initialize GUI Elements
		SelectorPanelController<Mod> selectorPanel = injector.getInstance(ModSelectorPanelFactory.class).create(new Dimension(800, 600), 0.35);
		ModListCellRenderer renderer = injector.getInstance(ModListCellRenderer.class);
		
		// Add Listeners
		modLoader.addListener(selectorPanel);
		modManager.addListener(renderer);

		// Start Application
		renderer.startFramerateTimer();
		modLoader.init();  // Load mods (will notify selector panel)
		
		try {	
			// Check for App update on Startup
			TinkerConfig config = injector.getInstance(TinkerConfig.class);
			if (config.isCheckForMMUpdatesOnStartup()){
				try {
					modManager.tryUpdateModManager();
				} catch (UnsupportedHostException | MalformedURLException e) {
					JOptionPane.showMessageDialog(null, e.toString(), "Error Checking for App Updates", JOptionPane.ERROR_MESSAGE);
				}
			}
			
			// Check for Mod Updates on Startup
			if (config.autoCheckForModUpdates()){
				modManager.checkForModUpdates();
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.toString(), "Error Checking for Mod Updates", JOptionPane.ERROR_MESSAGE);
		}
		
		// Initialize Frame
		MenuFactory menuFactory = injector.getInstance(MenuFactory.class);
		JFrame frame = new JFrame(TinkerTime.FULL_NAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setIconImages(Icons.getAppIcons());
		frame.setJMenuBar(menuFactory.createMenuBar());
		frame.add(menuFactory.createToolBar(), BorderLayout.NORTH);
		frame.add(selectorPanel.getComponent(), BorderLayout.CENTER);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
