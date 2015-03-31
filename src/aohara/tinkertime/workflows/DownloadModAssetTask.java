package aohara.tinkertime.workflows;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import aohara.common.workflows.tasks.FileTransferTask;
import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.resources.ModLoader;


class DownloadModAssetTask extends FileTransferTask {
	
	public static enum ModDownloadType { File, Image };
	
	private final Crawler<?> crawler;
	private final ModDownloadType type;
	private final TinkerConfig config;
	private final ModLoader modLoader;
	
	DownloadModAssetTask(Crawler<?> crawler, TinkerConfig config, ModLoader modLoader, ModDownloadType type){
		super(null, null);
		this.crawler = crawler;
		this.modLoader = modLoader;
		this.config = config;
		this.type = type;
	}
	
	private URL getUrl() throws IOException{
		switch(type){
		case File: return crawler.getDownloadLink();
		case Image: return crawler.getImageUrl();
		default: throw new IllegalStateException();
		}
	}
	
	private Path getDest() throws IOException{
		Mod mod = crawler.createMod();
		switch(type){
		case File: return modLoader.getZipPath(mod);
		case Image: return mod.getCachedImagePath(config);
		default: throw new IllegalStateException();
		}
	}

	@Override
	public boolean execute() throws Exception {
		transfer(
			getUrl() != null ? getUrl().toURI() : null,
			getDest()
		);
		
		setResult(crawler.createMod());
		return true;
	}

	@Override
	protected int findTargetProgress() throws IOException {
		URL url = getUrl();
		if (url != null){
			return url.openConnection().getContentLength();
		}
		return -1;
	}
}
