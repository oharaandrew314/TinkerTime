package io.andrewohara.tinkertime.controllers;

import io.andrewohara.tinkertime.controllers.coordinators.ModUpdateHandler;
import io.andrewohara.tinkertime.models.Installation;
import io.andrewohara.tinkertime.models.Mod;

import java.util.List;

public interface ModLoader extends ModUpdateHandler {

	public Installation getInstallation();
	public List<Mod> getMods();

}
