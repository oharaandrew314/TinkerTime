package aohara.tinkertime.controllers;

import aohara.tinkertime.models.Mod;

public interface ModUpdateHandler {
	
	public void modUpdated(Mod mod);
	public void modDeleted(Mod mod);
	
}
