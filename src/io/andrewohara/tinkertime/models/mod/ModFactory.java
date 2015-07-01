package io.andrewohara.tinkertime.models.mod;

import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateCoordinator;
import io.andrewohara.tinkertime.db.ModLoader;
import io.andrewohara.tinkertime.models.ConfigFactory;
import io.andrewohara.tinkertime.models.Installation;

import java.net.URL;

import com.google.inject.Inject;

public class ModFactory {

	private final ConfigFactory configFactory;
	private final ModUpdateCoordinator updateCoordinator;
	private final ModLoader modLoader;

	@Inject
	ModFactory(ConfigFactory configFactory, ModUpdateCoordinator updateCoordinator, ModLoader modLoader){
		this.configFactory = configFactory;
		this.updateCoordinator = updateCoordinator;
		this.modLoader = modLoader;
	}

	/*
	public Mod newLocalMod(Path zipPath){
		String fileName = zipPath.getFileName().toString();
		String prettyName = fileName;
		if (prettyName.indexOf(".") > 0) {
			prettyName = prettyName.substring(0, prettyName.lastIndexOf("."));
		}
		return new Mod(
				prettyName, fileName, null, null,
				Calendar.getInstance().getTime(), null,
				Version.valueOf(VersionParser.parseVersionString(prettyName))
				);
	}
	 */

	private Installation getInstallation(){
		return configFactory.getConfig().getSelectedInstallation();
	}

	public Mod newLocalMod(){
		Mod mod = new Mod(null, getInstallation());
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
