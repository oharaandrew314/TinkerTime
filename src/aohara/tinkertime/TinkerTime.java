package aohara.tinkertime;

import aohara.tinkertime.config.Config;
import aohara.tinkertime.controllers.ModDownloadManager;
import aohara.tinkertime.controllers.ModDownloadListener;
import aohara.tinkertime.models.ModApi;
import aohara.tinkertime.views.DirectoryChooser;

public class TinkerTime implements ModDownloadListener {
	
	public static final String NAME = "Tinker Time";
	private final ModDownloadManager downloadManager = new ModDownloadManager();
	
	public TinkerTime(){
		// Initialize Config
		Config config = new Config();
		if (config.getModsPath() == null || config.getKerbalPath() == null){
			new DirectoryChooser().setVisible(true);
		}
		
		downloadManager.addListener(this);
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
