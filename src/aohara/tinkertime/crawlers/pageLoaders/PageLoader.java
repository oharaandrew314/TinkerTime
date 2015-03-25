package aohara.tinkertime.crawlers.pageLoaders;

import java.io.IOException;
import java.net.URL;

import aohara.common.SimpleCache;

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
public abstract class PageLoader<T> {
	
	public static final int CACHING_TIME_MS = 10 * 60 * 1000;
	private final SimpleCache<URL, T> cache = new SimpleCache<>(CACHING_TIME_MS);
	
	protected abstract T loadPage(URL url) throws IOException;
	
	public final T getPage(URL url) throws IOException {
		if (!cache.containsKey(url)){
			cache.put(url, loadPage(url));
		}
		return cache.get(url);
	}
}
