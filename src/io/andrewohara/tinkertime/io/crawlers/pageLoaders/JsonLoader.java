package io.andrewohara.tinkertime.io.crawlers.pageLoaders;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * PageLoader for loading and caching Json Documents from the web.
 * 
 * @author Andrew O'Hara
 */
public class JsonLoader extends PageLoader<JsonElement> {

	private final JsonParser parser = new JsonParser();

	@Override
	protected JsonElement loadPage(URL url) throws IOException {
		try(Reader r = new InputStreamReader(url.openStream())){
			return parser.parse(r);
		}
	}
}
