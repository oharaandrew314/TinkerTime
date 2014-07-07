package aohara.tinkertime.models.context;

import aohara.common.executors.context.FileTransferContext;
import aohara.tinkertime.config.Config;
import aohara.tinkertime.models.Mod;

public class DownloadModContext extends FileTransferContext {
	
	public final Mod mod;

	public DownloadModContext(Mod mod, Config config) {
		super(mod.getDownloadLink(), config.getModZipPath(mod));
		this.mod = mod;
	}

}
