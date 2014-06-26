package aohara.tinkertime.controllers;

import aohara.tinkertime.models.ModApi;

public interface ModDownloadListener {
	
	public void modDownloadStarted(ModApi mod);
	public void modDownloadComplete(ModApi mod);
	public void modDownloadError(ModApi mod);

}
