package aohara.tinkertime.controllers;

import aohara.tinkertime.models.ModApi;

public interface ModDownloadListener {
	
	public void modDownloadStarted(ModApi mod, int numMods);
	public void modDownloadComplete(ModApi mod, int numMods);
	public void modDownloadError(ModApi mod, int numMods);

}
