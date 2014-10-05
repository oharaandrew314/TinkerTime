package test.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
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
			String fileName = FilenameUtils.getBaseName(url.toString());
			if (fileName.equals("releases")){
				try { // Chop off releases from path
					java.net.URI uri = url.toURI();
					uri = uri.getPath().endsWith("/") ? uri.resolve("..") : uri.resolve(".");
					String[] names = uri.getPath().split("/");
					fileName = names[names.length - 1];
				} catch (URISyntaxException e) {
					throw new IOException(e);
				}
			}
			
			try(InputStream is = ModLoader.class.getClassLoader().getResourceAsStream(String.format("test/res/html/%s.html", fileName))){
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