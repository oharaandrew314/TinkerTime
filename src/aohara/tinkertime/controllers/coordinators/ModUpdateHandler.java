package aohara.tinkertime.controllers.coordinators;

import aohara.tinkertime.models.Installation;
import aohara.tinkertime.models.Mod;

public interface ModUpdateHandler {

	public void updateMod(Mod mod);
	public void deleteMod(Mod mod);
	public void changeInstallation(Installation installation);

}
