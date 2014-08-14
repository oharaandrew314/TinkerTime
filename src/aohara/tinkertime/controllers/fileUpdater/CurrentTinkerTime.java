package aohara.tinkertime.controllers.fileUpdater;

import java.nio.file.Path;

import aohara.tinkertime.TinkerTime;

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
