package aohara.tinkertime.controllers;

import java.util.List;

import aohara.tinkertime.models.Installation;
import aohara.tinkertime.models.Mod;

public interface ModLoader extends ModUpdateHandler {

	public Installation getInstallation();
	public List<Mod> getMods();

}
