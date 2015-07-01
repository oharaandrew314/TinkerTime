package io.andrewohara.tinkertime.db;

import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateHandler;
import io.andrewohara.tinkertime.models.Installation;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.net.URL;
import java.util.List;

public interface ModLoader extends ModUpdateHandler {

	public Installation getInstallation();
	public List<Mod> getMods();
	public Mod get(int id);
	public Mod getByUrl(URL url);

}
