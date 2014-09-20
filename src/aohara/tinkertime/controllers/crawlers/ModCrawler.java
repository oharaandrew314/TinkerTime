package aohara.tinkertime.controllers.crawlers;

import java.net.URL;

import aohara.tinkertime.controllers.crawlers.pageLoaders.PageLoader;
import aohara.tinkertime.models.Mod;

public abstract class ModCrawler<T> extends Crawler<T> {

	public ModCrawler(URL url, PageLoader<T> pageLoader) {
		super(url, pageLoader);
	}
	
	public abstract Mod createMod() throws Exception;

}
