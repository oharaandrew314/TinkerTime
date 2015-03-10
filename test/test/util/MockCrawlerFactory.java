package test.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.google.gson.JsonElement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.CrawlerFactory;
import aohara.tinkertime.crawlers.pageLoaders.JsonLoader;
import aohara.tinkertime.crawlers.pageLoaders.PageLoader;

public class MockCrawlerFactory extends CrawlerFactory{
	
	@Override
	protected PageLoader<Document> createHtmlLoader(){
		return new MockWebpageLoader();
	}
	
	@Override
	protected PageLoader<JsonElement> createJsonLoader(){
		return new MockJsonLoader();
	}
	
	private static class MockWebpageLoader implements PageLoader<Document>{
		
		@Override
		public Document getPage(Crawler<Document> crawler, URL url) throws IOException {
			String resourceName = "html/" + url.toString().split("//")[1].replace("/", "-");
			try(InputStream is = TestModLoader.class.getClassLoader().getResourceAsStream(resourceName)){
				Document doc = Jsoup.parse(is, null, url.toString());
				return doc;
			}
		}
	}
	
	private static class MockJsonLoader implements PageLoader<JsonElement>{

		@Override
		public JsonElement getPage(Crawler<JsonElement> crawler, URL url) throws IOException {
			String resourceName = String.format("json/%s.json", crawler.generateId());
			return new JsonLoader().getPage(crawler, this.getClass().getClassLoader().getResource(resourceName));
		}
		
	}
}
