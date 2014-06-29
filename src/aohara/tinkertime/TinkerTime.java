package aohara.tinkertime;

import aohara.tinkertime.config.Config;
import aohara.tinkertime.controllers.ModDownloadListener;
import aohara.tinkertime.controllers.ModDownloadManager;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModApi;

public class TinkerTime implements ModDownloadListener {
	
	public static final String NAME = "Tinker Time";
	private final ModDownloadManager dm = new ModDownloadManager();
	private final ModStateManager stateManager;
	
	public TinkerTime(){		
		// Initialize Controllers
		stateManager = new ModStateManager(
			new Config().getModsPath().resolve("mods.json")
		);
		dm.addListener(this);
		
		System.out.println("Existing Mods:");
		for (Mod mod : stateManager.getMods()){
			System.out.println(mod.getName());
		}		
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
