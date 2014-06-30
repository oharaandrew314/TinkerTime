package aohara.tinkertime;

import aoahara.common.selectorPanel.SelectorPanel;
import aohara.tinkertime.config.Config;
import aohara.tinkertime.controllers.ModDownloadListener;
import aohara.tinkertime.controllers.ModDownloadManager;
import aohara.tinkertime.controllers.ModManager;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModApi;
import aohara.tinkertime.views.Frame;
import aohara.tinkertime.views.ModView;

public class TinkerTime implements ModDownloadListener {
	
	public static final String NAME = "Tinker Time";
	private final ModManager mm;
	
	public TinkerTime(){
		Config.verifyConfig();
		
		// Initialize Controllers
		Config config = new Config();
		ModDownloadManager dm = new ModDownloadManager();
		dm.addListener(this);
		ModStateManager sm = new ModStateManager(config.getModsPath().resolve("mods.json"));
		mm = new ModManager(sm, dm, config, null);  // FIXME: Implement CR
		
		// Initialize GUI
		SelectorPanel<Mod> sp = new SelectorPanel<Mod>(new ModView());
		sm.addListener(sp);
		sm.getMods();  // Load mods (will notify selector panel)
		
		new Frame(sp, mm);
	}

	@Override
	public void modDownloadStarted(ModApi mod) {
		System.out.println("Downloading " + mod.getNewestFile());
	}

	@Override
	public void modDownloadComplete(ModApi mod) {
		System.out.println("Finished Downloading " + mod.getNewestFile());
	}

	@Override
	public void modDownloadError(ModApi mod) {
		System.out.println("Error downloading " + mod.getNewestFile());
	}
	
	public static void main(String[] args) {
		new TinkerTime();
	}

}
