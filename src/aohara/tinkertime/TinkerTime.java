package aohara.tinkertime;

import aohara.common.selectorPanel.SelectorPanel;
import aohara.common.workflows.ProgressPanel;
import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.controllers.fileUpdater.ModuleManagerUpdateController;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModComparator;
import aohara.tinkertime.views.Frame;
import aohara.tinkertime.views.ModImageView;
import aohara.tinkertime.views.ModListCellRenderer;
import aohara.tinkertime.views.ModView;
import aohara.tinkertime.views.TinkerMenuBar;

/**
 * Controller which starts the Application.
 * 
 * @author Andrew O'Hara
 */
public class TinkerTime {
	
	public static final String
		NAME = "Tinker Time",
		VERSION = "0.6",
		AUTHOR = "Andrew O'Hara";
	
	private final ModManager mm;
	
	public TinkerTime(){
		Config config = new Config();
		config.verifyConfig();
		
		ProgressPanel pp = new ProgressPanel();
		
		// Initialize Controllers
		ModStateManager sm = new ModStateManager(config);
		mm = ModManager.createDefaultModManager(sm, pp);
		
		// Initialize GUI
		SelectorPanel<Mod> sp = new SelectorPanel<Mod>(new ModView(), new ModComparator());
		sp.addControlPanel(true, new ModImageView(config));
		sp.setListCellRenderer(new ModListCellRenderer());
		TinkerMenuBar menuBar = new TinkerMenuBar(mm);
		
		// Add Listeners
		sp.addListener(mm);
		sp.addListener(menuBar);
		sm.addListener(sp);

		// Start Application
		sm.getMods();  // Load mods (will notify selector panel)
		new Frame(mm, sp, pp, menuBar);
		
		// Launch Startup Tasks
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
	
	public static void main(String[] args) {
		new TinkerTime();
	}
}
