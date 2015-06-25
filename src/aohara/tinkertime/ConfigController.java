package aohara.tinkertime;

import com.google.inject.Inject;

import aohara.common.config.OptionsWindow;
import aohara.tinkertime.controllers.ModUpdateCoordinator;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.resources.ModMetaLoader;

public class ConfigController {
	
	private final TinkerConfig config;
	private final ModMetaLoader modLoader;
	private final ModUpdateCoordinator updateCoordinator;
	
	@Inject
	ConfigController(TinkerConfig config, ModMetaLoader modLoader, ModUpdateCoordinator updateCoordinator) {
		this.config = config;
		this.modLoader = modLoader;
		this.updateCoordinator = updateCoordinator;
	}
	
	public void openConfigDialog() {
		new OptionsWindow(config.config).toDialog();
		reloadMods();
	}
	
	public void reloadMods(){
		modLoader.clear();
		for (Mod modToLoad : modLoader.getMods()){
			updateCoordinator.modUpdated(modLoader, modToLoad);
		}
	}

}
