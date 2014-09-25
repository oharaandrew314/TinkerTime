package aohara.tinkertime.crawlers;

import java.io.IOException;
import java.net.URL;

import aohara.tinkertime.crawlers.pageLoaders.PageLoader;
import aohara.tinkertime.models.Mod;

/**
 * Crawler designed for crawling mods.
 * 
 * Can create a Mod model from the page.
 * 
 * @author Andrew O'Hara
 *
 * @param <T> Type of PageLoader used to load the page
 */
public abstract class ModCrawler<T> extends Crawler<T> {

	public ModCrawler(URL url, PageLoader<T> pageLoader) {
		super(url, pageLoader);
	}
	
	public abstract String getName() throws IOException;
	public abstract URL getImageUrl() throws IOException;
	public abstract Mod createMod() throws IOException;

}
