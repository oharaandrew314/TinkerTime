package aohara.tinkertime.crawlers.pageLoaders;

import java.io.IOException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
	
/**
 * PageLoader for loading and caching HTML documents from the web.
 * 
 * @author Andrew O'Hara
 */
public class WebpageLoader extends PageLoader<Document>{
	
	private static final int TIMEOUT_MS = 10 * 1000;

	@Override
	protected Document loadPage(String pageId, URL url) throws IOException {
		return Jsoup.parse(url, TIMEOUT_MS);
	}
}
