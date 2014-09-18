package aohara.tinkertime.controllers.fileUpdater;

import java.nio.file.Path;

/**
 * Public Strategy Interface for obtaining the currently installed version of a file.
 * 
 * @author Andrew O'Hara
 */
public interface CurrentVersion {
	public String getVersion();
	public Path getPath();
	public boolean exists();
}
