package io.andrewohara.tinkertime.models.mod;

import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateCoordinatorImpl;
import io.andrewohara.tinkertime.db.ConfigFactory;
import io.andrewohara.tinkertime.db.ModLoader;
import io.andrewohara.tinkertime.models.Installation;

import java.net.URL;
import java.nio.file.Path;

import com.google.inject.Inject;

public class ModFactory {

	private final ConfigFactory configFactory;
	private final ModUpdateCoordinatorImpl updateCoordinator;
	private final ModLoader modLoader;

	@Inject
	ModFactory(ConfigFactory configFactory, ModUpdateCoordinatorImpl updateCoordinator, ModLoader modLoader){
		this.configFactory = configFactory;
		this.updateCoordinator = updateCoordinator;
		this.modLoader = modLoader;
	}

	private Installation getInstallation(){
		return configFactory.getConfig().getSelectedInstallation();
	}

	public Mod newLocalMod(Path path){
		Mod mod = new Mod(null, getInstallation());
		mod.update(path.getFileName().toString(), null, null, null, null);
		updateCoordinator.updateMod(mod);
		return mod;
	}

	public Mod newMod(URL url) {
		Mod mod = modLoader.getByUrl(url);
		if (mod == null){
			mod = new Mod(url, getInstallation());
			updateCoordinator.updateMod(mod);
			return mod;
		}
		return mod;
	}
}
