package aohara.tinkertime.models.context;

import aohara.common.executors.context.FileTransferContext;
import aohara.tinkertime.config.Config;
import aohara.tinkertime.models.ModApi;

public class DownloadModContext extends FileTransferContext {
	
	public final ModApi modApi;

	public DownloadModContext(ModApi modApi, Config config) {
		super(modApi.getDownloadLink(), config.getModZipPath(modApi));
		this.modApi = modApi;
	}

}
