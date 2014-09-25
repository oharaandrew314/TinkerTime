package aohara.tinkertime.crawlers.pageLoaders;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
	
/**
 * PageLoader for loading and caching HTML documents from the web.
 * 
 * @author Andrew O'Hara
 */
public class WebpageLoader implements PageLoader<Document>{
	
	public static final int TIMEOUT_MS = 5000;
		
	private final Map<URL, Document> documentCache = new HashMap<>();
	
	public Document getPage(URL url) throws IOException {
		if (!documentCache.containsKey(url)){
			documentCache.put(url, Jsoup.parse(url, TIMEOUT_MS));
		}
		return documentCache.get(url);
	}
}
