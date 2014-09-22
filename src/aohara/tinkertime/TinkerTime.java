package aohara.tinkertime;

import java.awt.event.MouseEvent;

import aohara.common.selectorPanel.ListListener;
import aohara.common.selectorPanel.SelectorPanel;
import aohara.common.workflows.ProgressPanel;
import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.models.Mod;
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
public class TinkerTime implements ListListener<Mod> {
	
	public static final String
		NAME = "Tinker Time",
		VERSION = "0.6",
		AUTHOR = "Andrew O'Hara";
	
	private final ModManager mm;
	
	public TinkerTime(){
		Config.verifyConfig();
		Config config = new Config();
		
		ProgressPanel pp = new ProgressPanel();
		
		// Initialize Controllers
		ModStateManager sm = new ModStateManager(config.getModsZipPath().resolve("mods.json"));
		mm = ModManager.createDefaultModManager(sm, pp);
		
		// Initialize GUI
		SelectorPanel<Mod> sp = new SelectorPanel<Mod>(new ModView());
		sp.addControlPanel(true, new ModImageView(config));
		sp.setListCellRenderer(new ModListCellRenderer());
		TinkerMenuBar menuBar = new TinkerMenuBar(mm);		
		
		// Add Listeners
		sp.addListener(this);
		sp.addListener(menuBar);
		sm.addListener(sp);

		// Start Application
		sm.getMods();  // Load mods (will notify selector panel)
		new Frame(mm, sp, pp, menuBar);
	}
	
	public static void main(String[] args) {
		new TinkerTime();
	}

	@Override
	// TODO: Move out of this class
	public void elementClicked(Mod mod, int numTimes) throws Exception{
		if (numTimes == 2){
			if (mod.isEnabled()){
				mm.disableMod(mod);
			} else {
				mm.enableMod(mod);
			}
		}
	}

	@Override
	public void elementSelected(Mod element) {
		// Do Nothing
	}

	@Override
	public void elementRightClicked(MouseEvent evt, Mod mod) throws Exception {
		// Do Nothing
	}
}
