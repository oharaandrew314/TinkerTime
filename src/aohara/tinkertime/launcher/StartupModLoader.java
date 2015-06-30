package aohara.tinkertime.launcher;

import aohara.tinkertime.controllers.ModManager;

import com.google.inject.Inject;

public class StartupModLoader implements Runnable {

	private final ModManager modManager;

	@Inject
	StartupModLoader(ModManager modManager){
		this.modManager = modManager;
	}

	@Override
	public void run() {
		modManager.reloadMods();
	}

}
