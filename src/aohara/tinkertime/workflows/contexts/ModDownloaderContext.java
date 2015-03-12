package aohara.tinkertime.workflows.contexts;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Calendar;

import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.CrawlerFactory;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.models.Mod;

public class ModDownloaderContext extends DownloaderContext {
	
	private static final CrawlerFactory factory = new CrawlerFactory();
	private final TinkerConfig config;
	private Mod cachedMod;
	
	private ModDownloaderContext(Crawler<?> crawler, TinkerConfig config){
		super(crawler);
		this.config = config;
	}
	
	public static ModDownloaderContext create(URL modUrl, TinkerConfig config) throws UnsupportedHostException{
		return new ModDownloaderContext(factory.getCrawler(modUrl), config);
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
		if (cachedMod != null){
			cachedMod = new Mod(
				crawler.generateId(), crawler.getName(), crawler.getNewestFileName(),
				crawler.getCreator(), crawler.getImageUrl(), crawler.getPageUrl(),
				crawler.getUpdatedOn() != null ? crawler.getUpdatedOn() : Calendar.getInstance().getTime(),
				crawler.getSupportedVersion()
			);
		}
		return cachedMod;
	}
}
