package aohara.tinkertime.controllers.files;

import java.nio.file.Path;

import aohara.tinkertime.models.Mod;

public abstract class ConflictResolver {
	
	public enum Resolution { Skip, Cancel, Overwrite };
	
	public abstract Resolution getResolution(Path Conflict, Mod mod);

}
