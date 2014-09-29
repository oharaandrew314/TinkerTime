package aohara.tinkertime.controllers;

import aohara.tinkertime.models.Mod;

/**
 * Public Interface for Classes which must be notified of a change in a Mod's information or state.
 * 
 * @author Andrew O'Hara
 */
public interface ModUpdateListener {
	public void modUpdated(Mod mod, boolean deleted);
}
