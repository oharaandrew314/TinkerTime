package test.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.CrawlerFactory;
import aohara.tinkertime.crawlers.pageLoaders.JsonLoader;
import aohara.tinkertime.crawlers.pageLoaders.PageLoader;

import com.google.gson.JsonObject;

public class MockCrawlerFactory extends CrawlerFactory{
	
	@Override
	protected PageLoader<Document> createHtmlLoader(){
		return new MockWebpageLoader();
	}
	
	@Override
	protected PageLoader<JsonObject> createJsonLoader(){
		return new MockJsonLoader();
	}
	
	private static class MockWebpageLoader implements PageLoader<Document>{
		
		@Override
		public Document getPage(Crawler<Document> crawler, URL url) throws IOException {
			String resourceName = String.format("test/res/html/%s.html", crawler.generateId());
			try(InputStream is = ModLoader.class.getClassLoader().getResourceAsStream(resourceName)){
				return Jsoup.parse(is, null, url.toString());
			}
		}
	}
	
	private static class MockJsonLoader implements PageLoader<JsonObject>{

		@Override
		public JsonObject getPage(Crawler<JsonObject> crawler, URL url) throws IOException {			
			String resourceName = String.format("test/res/json/%s.json", crawler.generateId());
			return new JsonLoader().getPage(crawler, this.getClass().getClassLoader().getResource(resourceName));
		}
		
	}
}