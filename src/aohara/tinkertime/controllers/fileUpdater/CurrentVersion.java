package aohara.tinkertime.controllers.fileUpdater;

import java.nio.file.Path;

public interface CurrentVersion {
	public String getVersion();
	public Path getPath();
	public boolean exists();
}
