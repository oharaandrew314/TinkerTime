package aohara.tinkertime.controllers.crawlers.pageLoaders;

import java.io.IOException;
import java.net.URL;

public interface PageLoader<T> {
	public T getPage(URL url) throws IOException;
}


