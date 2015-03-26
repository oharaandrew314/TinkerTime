package aohara.tinkertime.workflows.contexts;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Calendar;

import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.models.Mod;

public class ModDownloaderContext extends DownloaderContext {
	
	private final TinkerConfig config;
	private Mod cachedMod;
	
	public ModDownloaderContext(Crawler<?> crawler, TinkerConfig config){
		super(crawler);
		this.config = config;
	}
	
	@Override
	public Path getCachedImagePath() throws IOException{
		return createMod().getCachedImagePath(config);
	}
	
	@Override
	public Path getDownloadPath() throws IOException {
		return createMod().getCachedZipPath(config);
	}
	
	public Mod createMod() throws IOException{
		if (cachedMod == null){
			cachedMod = new Mod(
				crawler.generateId(), crawler.getName(), crawler.getNewestFileName(),
				crawler.getCreator(), crawler.getPageUrl(),
				crawler.getUpdatedOn() != null ? crawler.getUpdatedOn() : Calendar.getInstance().getTime(),
				crawler.getSupportedVersion()
			);
		}
		return cachedMod;
	}
}
