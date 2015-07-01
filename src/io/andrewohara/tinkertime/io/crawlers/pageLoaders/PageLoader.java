package io.andrewohara.tinkertime.io.crawlers.pageLoaders;

import io.andrewohara.common.content.ExpiryCache;

import java.io.IOException;
import java.net.URL;

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
	private final ExpiryCache<URL, T> cache = new ExpiryCache<>(CACHING_TIME_MS);  // TODO Investigate Guava Cache

	public PageLoader(){
		System.setProperty("http.agent", "TinkerTime Mod Manager Agent");
	}

	protected abstract T loadPage(URL url) throws IOException;

	public final T getPage(URL url) throws IOException {
		if (!cache.containsKey(url)){
			cache.put(url, loadPage(url));
		}
		return cache.get(url);
	}
}
