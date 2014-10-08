package aohara.tinkertime.crawlers.pageLoaders;

import java.io.IOException;
import java.net.URL;

import aohara.tinkertime.crawlers.Crawler;

/**
 * Public Interface used by the Crawler class for acquiring Pages.
 * 
 * It is reccomended that the implementing Class use its own caching method
 * for caching pages, if necessary.
 * 
 * @author Andrew O'Hara
 *
 * @param <T> Model which contains the Page
 */
public interface PageLoader<T> {
	public T getPage(Crawler<T> crawler, URL url) throws IOException;
}


