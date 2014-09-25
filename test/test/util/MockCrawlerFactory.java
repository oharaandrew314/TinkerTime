package test.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
		public Document getPage(URL url) throws IOException {
			String filePath = String.format("test/res/html/%s.html", FilenameUtils.getBaseName(url.toString()));
			try(InputStream is = ModLoader.class.getClassLoader().getResourceAsStream(filePath)){
				return Jsoup.parse(is, null, url.toString());
			}
		}
	}
	
	private static class MockJsonLoader implements PageLoader<JsonObject>{

		@Override
		public JsonObject getPage(URL url) throws IOException {			
			return new JsonLoader().getPage(this.getClass().getClassLoader().getResource(
					String.format("test/res/json/%s.json", FilenameUtils.getBaseName(url.toString())
			)));
		}
		
	}
}