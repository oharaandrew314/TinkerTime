package aohara.tinkertime.controllers.fileUpdater;

import java.nio.file.Path;

import aohara.tinkertime.TinkerTime;

/**
 * Strategy for obtaining the currently installed version of Tinker Time.
 * @author Andrew O'Hara
 *
 */
public class CurrentTinkerTime implements CurrentVersion {

	@Override
	public String getVersion() {
		return TinkerTime.VERSION;
	}

	@Override
	public Path getPath() {
		return null;
	}

	@Override
	public boolean exists() {
		return true;
	}

}
