package aohara.tinkertime;

import java.awt.BorderLayout;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.github.zafarkhaja.semver.Version;

import aohara.common.selectorPanel.SelectorPanelBuilder;
import aohara.common.selectorPanel.SelectorPanelController;
import aohara.common.views.ProgressPanel;
import aohara.tinkertime.crawlers.CrawlerFactory;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.crawlers.pageLoaders.JsonLoader;
import aohara.tinkertime.crawlers.pageLoaders.WebpageLoader;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.resources.ModLoader;
import aohara.tinkertime.views.TinkerFrame;
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
	public static final Version VERSION = Version.valueOf("1.3.0");
	public static final String
		SAFE_NAME = NAME.replace(" ", ""),
		FULL_NAME = String.format("%s v%s by %s", NAME, VERSION, AUTHOR);
	
	public static void main(String[] args) {
		TinkerConfig config = TinkerConfig.create();
		
		// Initialize Controllers
		ModLoader modLoader = new ModLoader(config);
		ModManager modManager = new ModManager(
			modLoader,
			config,
			(ThreadPoolExecutor) Executors.newFixedThreadPool(config.numConcurrentDownloads()),
			(Executor) Executors.newSingleThreadExecutor(),
			new CrawlerFactory(new WebpageLoader(), new JsonLoader())
		);
		ModListListener listListener = new ModListListener(modManager);
		
		// Set HTTP User-agent
		System.setProperty("http.agent", "TinkerTime Bot");
		
		// Initialize GUI		
		SelectorPanelBuilder<Mod> spBuilder = new SelectorPanelBuilder<>();
		ModListCellRenderer renderer = ModListCellRenderer.create(modLoader);
		spBuilder.setListCellRenderer(renderer);
		spBuilder.setContextMenu(MenuFactory.createPopupMenu(modManager));
		spBuilder.addKeyListener(listListener);
		spBuilder.addSelectionListener(listListener);
		SelectorPanelController<Mod> selectorPanel = spBuilder.createSelectorPanel(new ModView(modLoader, config));
		ProgressPanel progessPanel = new ProgressPanel();
		
		// Add Listeners
		modLoader.addListener(selectorPanel);
		modManager.addListener(progessPanel);
		modManager.addListener(renderer);

		// Start Application
		renderer.startFramerateTimer();
		modLoader.init(modManager);  // Load mods (will notify selector panel)
		
		// Check for App update on Startup
		if (config.isCheckForMMUpdatesOnStartup()){
			try {
				modManager.tryUpdateModManager();
			} catch (UnsupportedHostException e) {
				JOptionPane.showMessageDialog(null, e.toString(), "Error Checking for App Updates", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		// Check for Mod Updates on Startup
		try {			
			if (config.autoCheckForModUpdates()){
				modManager.checkForModUpdates();
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.toString(), "Error Checking for Mod Updates", JOptionPane.ERROR_MESSAGE);
		}
		
		// Initialize Frame
		JFrame frame = new TinkerFrame();
		frame.setJMenuBar(MenuFactory.createMenuBar(modManager));
		frame.add(MenuFactory.createToolBar(modManager), BorderLayout.NORTH);
		frame.add(selectorPanel.getComponent(), BorderLayout.CENTER);
		frame.add(progessPanel.getComponent(), BorderLayout.SOUTH);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
