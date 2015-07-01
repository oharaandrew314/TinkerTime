package io.andrewohara.tinkertime.controllers.coordinators;

import io.andrewohara.tinkertime.models.Installation;
import io.andrewohara.tinkertime.models.mod.Mod;

public interface ModUpdateHandler {

	public void updateMod(Mod mod);
	public void deleteMod(Mod mod);
	public void changeInstallation(Installation installation);

}
