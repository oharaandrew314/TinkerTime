package aohara.tinkertime;

import java.awt.event.MouseEvent;

import aohara.common.executors.Downloader;
import aohara.common.executors.FileTransferExecutor.FileConflictResolver;
import aohara.common.executors.TempDownloader;
import aohara.common.executors.progress.ProgressDialog;
import aohara.common.selectorPanel.ListListener;
import aohara.common.selectorPanel.SelectorPanel;
import aohara.tinkertime.config.Config;
import aohara.tinkertime.controllers.ModEnabler;
import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.views.DialogConflictResolver;
import aohara.tinkertime.views.Frame;
import aohara.tinkertime.views.ModImageView;
import aohara.tinkertime.views.ModListCellRenderer;
import aohara.tinkertime.views.ModView;
import aohara.tinkertime.views.TinkerMenuBar;

public class TinkerTime implements ListListener<Mod> {
	
	public static final String
		NAME = "Tinker Time",
		VERSION = "0.4",
		AUTHOR = "Andrew O'Hara";
	private final ModManager mm;
	
	public TinkerTime(){
		Config.verifyConfig();
		
		// Initialize Controllers
		Config config = new Config();	
		Downloader pageDownloader = new Downloader(ModManager.NUM_CONCURRENT_DOWNLOADS, FileConflictResolver.Overwrite);
		Downloader modDownloader = new TempDownloader(ModManager.NUM_CONCURRENT_DOWNLOADS, FileConflictResolver.Overwrite);
		ModStateManager sm = new ModStateManager(config.getModsPath().resolve("mods.json"));
		ModEnabler enabler = new ModEnabler(new DialogConflictResolver(config, sm));
		mm = new ModManager(sm, config, pageDownloader, modDownloader, enabler);
		
		// Initialize GUI
		SelectorPanel<Mod> sp = new SelectorPanel<Mod>(new ModView());
		sp.addControlPanel(true, new ModImageView());
		sp.setListCellRenderer(new ModListCellRenderer());
		TinkerMenuBar menuBar = new TinkerMenuBar(mm);		
		
		// Add Listeners
		sp.addListener(this);
		sp.addListener(menuBar);
		pageDownloader.addListener(new ProgressDialog("Downloading Mod Pages"));
		enabler.addListener(new ProgressDialog("Processing Mods"));
		modDownloader.addListener(new ProgressDialog("Downloading Mods"));
		sm.addListener(sp);

		// Start Application
		sm.getMods();  // Load mods (will notify selector panel)
		new Frame(mm, sp, menuBar);
	}
	
	public static void main(String[] args) {
		new TinkerTime();
	}

	@Override
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
