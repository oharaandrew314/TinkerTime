package aohara.tinkertime;

import javax.swing.JOptionPane;

import aohara.common.selectorPanel.ListListener;
import aohara.common.selectorPanel.SelectorPanel;
import aohara.tinkertime.config.Config;
import aohara.tinkertime.controllers.ModDownloadManager;
import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.controllers.ModManager.CannotDisableModException;
import aohara.tinkertime.controllers.ModManager.CannotEnableModException;
import aohara.tinkertime.controllers.ModManager.ModAlreadyDisabledException;
import aohara.tinkertime.controllers.ModManager.ModAlreadyEnabledException;
import aohara.tinkertime.controllers.ModManager.ModNotDownloadedException;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.views.Frame;
import aohara.tinkertime.views.ModImageView;
import aohara.tinkertime.views.ModListCellRenderer;
import aohara.tinkertime.views.ModView;
import aohara.tinkertime.views.StatusBar;
import aohara.tinkertime.views.TinkerMenuBar;

public class TinkerTime implements ListListener<Mod> {
	
	public static final String NAME = "Tinker Time";
	private final ModManager mm;
	
	public TinkerTime(){
		Config.verifyConfig();
		
		// Initialize Controllers
		Config config = new Config();
		ModDownloadManager dm = new ModDownloadManager();
		ModStateManager sm = new ModStateManager(config.getModsPath().resolve("mods.json"));
		mm = new ModManager(sm, dm, config, null);  // FIXME: Implement CR
		
		// Initialize GUI
		SelectorPanel<Mod> sp = new SelectorPanel<Mod>(new ModView());
		sp.addControlPanel(true, new ModImageView());
		sp.setListCellRenderer(new ModListCellRenderer());
		StatusBar statusBar = new StatusBar();
		TinkerMenuBar menuBar = new TinkerMenuBar(mm, sm);		
		
		// Add Listeners
		sp.addListener(this);
		sp.addListener(menuBar);
		dm.addListener(statusBar);
		sm.addListener(sp);

		// Start Application
		sm.getMods();  // Load mods (will notify selector panel)
		new Frame(mm, sp, statusBar, menuBar);
	}
	
	public static void main(String[] args) {
		new TinkerTime();
	}

	@Override
	public void elementClicked(Mod mod, int numTimes) {
		if (numTimes == 2){
			if (mod.isEnabled()){
				try {
					mm.disableMod(mod);
				} catch (ModAlreadyDisabledException
						| CannotDisableModException e) {
					errorMessage("Could not disable " + mod.getName());
				}
			} else {
				try {
					mm.enableMod(mod);
				} catch (ModAlreadyEnabledException | ModNotDownloadedException
						| CannotEnableModException | CannotDisableModException e) {
					errorMessage("Could not enable " + mod.getName());
				}
			}
		}
		
	}
	
	private void errorMessage(String message){
		JOptionPane.showMessageDialog(
			null, message, "Error!", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void elementSelected(Mod element) {
		// Do Nothing
	}

}
