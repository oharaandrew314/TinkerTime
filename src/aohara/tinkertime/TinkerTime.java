package aohara.tinkertime;

import java.io.IOException;

import aohara.tinkertime.config.Config;
import aohara.tinkertime.controllers.DownloadManager;
import aohara.tinkertime.controllers.ModDownloadListener;
import aohara.tinkertime.models.ModApi;
import aohara.tinkertime.models.ModPage;
import aohara.tinkertime.views.DirectoryChooser;

public class TinkerTime implements ModDownloadListener {
	
	public static final String NAME = "Tinker Time";
	private final DownloadManager downloadManager = new DownloadManager();
	
	public TinkerTime(){
		// Initialize Config
		Config config = new Config();
		if (config.getModsPath() == null || config.getKerbalPath() == null){
			new DirectoryChooser().setVisible(true);
		}
		
		downloadManager.addListener(this);
		
		
		// Test Code
		try {
			ModPage mechJeb = new ModPage("http://www.curse.com/ksp-mods/kerbal/220221-mechjeb");
			ModPage engineer = new ModPage("http://www.curse.com/ksp-mods/kerbal/220285-kerbal-engineer-redux");
			
			downloadManager.downloadMod(mechJeb);
			downloadManager.downloadMod(engineer);
			
		} catch (IOException e) {
			e.printStackTrace();
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
