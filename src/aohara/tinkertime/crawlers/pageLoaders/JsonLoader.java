package aohara.tinkertime.crawlers.pageLoaders;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import aohara.tinkertime.crawlers.Crawler;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * PageLoader for loading and caching Json Documents from the web.
 * 
 * @author Andrew O'Hara
 */
public class JsonLoader implements PageLoader<JsonElement> {
	
	private final Map<URL, JsonElement> cache = new HashMap<>();
	private final JsonParser parser = new JsonParser();

	@Override
	public JsonElement getPage(Crawler<JsonElement> crawler, URL url) throws IOException {
		if (!cache.containsKey(url)){
			try(Reader r = new InputStreamReader(url.openStream())){
                JsonElement jsonElement = parser.parse(r);
				cache.put(url, jsonElement);
			}
		}
		return cache.get(url);
	}

}
