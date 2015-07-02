package io.andrewohara.tinkertime.io.crawlers.pageLoaders;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

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

	public static final int CACHING_EXIPIRY_MINUTES = 1;
	private final LoadingCache<URL, T> cache;

	public PageLoader(){
		System.setProperty("http.agent", "TinkerTime Mod Manager Agent");
		cache = CacheBuilder.newBuilder()
				.expireAfterAccess(CACHING_EXIPIRY_MINUTES, TimeUnit.MINUTES)
				.build(
						new CacheLoader<URL, T>() {
							@Override
							public T load(URL url) throws IOException {
								return loadPage(url);
							}
						});
	}

	protected abstract T loadPage(URL url) throws IOException;

	public final T getPage(URL url) throws IOException {
		try {
			return cache.get(url);
		} catch (ExecutionException e) {
			throw new IOException(e);
		}
	}
}
