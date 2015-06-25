package aohara.tinkertime.testutil;

import java.io.IOException;
import java.nio.file.Path;

import aohara.tinkertime.controllers.ModMetaHelper;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.CrawlerFactory;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.resources.ModStructure;

import com.google.inject.Inject;

public class ResourceLoader {
	
	private final ModMetaHelper modHelper;
	private final CrawlerFactory crawlerFactory;
	
	@Inject
	ResourceLoader(ModMetaHelper modHelper, CrawlerFactory crawlerFactory){
		this.modHelper = modHelper;
		this.crawlerFactory = crawlerFactory;
	}
	
	private Crawler<?> loadCrawler(ModStubs stub){
		try {
			Crawler<?> crawler = crawlerFactory.getCrawler(stub.url);
			crawler.setAssetSelector(new StaticAssetSelector());
			return crawler;
		} catch (UnsupportedHostException e) {
			throw new RuntimeException(String.format("ModStub: %s has an unsupported URL", stub));
		}
	}
	
	public Mod loadMod(ModStubs stub){
		try {
			return loadCrawler(stub).call();
		} catch (IOException e) {
			throw new RuntimeException("Error creating mod for stub: " + stub.name, e);
		}
	}
	
	public Path getZipPath(Mod mod){
		Path path = modHelper.getZipPath(mod);
		if (modHelper.isDownloaded(mod)){
			return path;
		}
		throw new RuntimeException(String.format("Mod zip not found: %s", path));
	}
	
	public Path getZipPath(ModStubs stub){
		return getZipPath(loadMod(stub));
	}
	
	public ModStructure getStructure(ModStubs stub) {
		return new ModStructure(modHelper.getZipPath(loadMod(stub)));
	}
}
