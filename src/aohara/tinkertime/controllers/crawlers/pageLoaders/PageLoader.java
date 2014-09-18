package aohara.tinkertime.controllers.crawlers.pageLoaders;

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
public interface PageLoader<T> {
	public T getPage(URL url) throws IOException;
}


