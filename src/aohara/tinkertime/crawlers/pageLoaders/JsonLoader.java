package aohara.tinkertime.crawlers.pageLoaders;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import aohara.tinkertime.crawlers.Crawler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * PageLoader for loading and caching Json Documents from the web.
 * 
 * @author Andrew O'Hara
 */
public class JsonLoader implements PageLoader<JsonObject> {
	
	private final Map<URL, JsonObject> cache = new HashMap<>();
	private final JsonParser parser = new JsonParser();

	@Override
	public JsonObject getPage(Crawler<JsonObject> crawler, URL url) throws IOException {
		if (!cache.containsKey(url)){
			try(Reader r = new InputStreamReader(url.openStream())){
				return parser.parse(r).getAsJsonObject();
			}
		}
		return cache.get(url);
	}

}
