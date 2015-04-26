package aohara.tinkertime;

import java.awt.BorderLayout;
import java.net.MalformedURLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import aohara.common.selectorPanel.SelectorPanelBuilder;
import aohara.common.selectorPanel.SelectorPanelController;
import aohara.tinkertime.crawlers.CrawlerFactory;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.crawlers.pageLoaders.JsonLoader;
import aohara.tinkertime.crawlers.pageLoaders.WebpageLoader;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.resources.Icons;
import aohara.tinkertime.resources.ModLoader;
import aohara.tinkertime.views.ModListCellRenderer;
import aohara.tinkertime.views.ModView;
import aohara.tinkertime.views.menus.MenuFactory;

import com.github.zafarkhaja.semver.Version;

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
	public static final Version VERSION = Version.valueOf("1.4.0");
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
		
		// Add Listeners
		modLoader.addListener(selectorPanel);
		modManager.addListener(renderer);
		new AddModDragDropHandler(selectorPanel.getList(), modManager);  // Add Mod Drag and Drop Handler

		// Start Application
		renderer.startFramerateTimer();
		modLoader.init(modManager);  // Load mods (will notify selector panel)
		
		// Check for App update on Startup
		if (config.isCheckForMMUpdatesOnStartup()){
			try {
				modManager.tryUpdateModManager();
			} catch (UnsupportedHostException | MalformedURLException e) {
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
		JFrame frame = new JFrame(TinkerTime.FULL_NAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setIconImages(Icons.getAppIcons());
		frame.setJMenuBar(MenuFactory.createMenuBar(modManager));
		frame.add(MenuFactory.createToolBar(modManager), BorderLayout.NORTH);
		frame.add(selectorPanel.getComponent(), BorderLayout.CENTER);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
