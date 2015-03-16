package test.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import test.UnitTestSuite;
import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.crawlers.CrawlerFactory;
import aohara.tinkertime.crawlers.pageLoaders.JsonLoader;
import aohara.tinkertime.crawlers.pageLoaders.PageLoader;

import com.google.gson.JsonElement;

public class MockHelper {
	
	public static CrawlerFactory newCrawlerFactory(){
		return new CrawlerFactory(
			new PageLoader<Document>(){
				@Override
				protected Document loadPage(String pageId, URL url) throws IOException {
					String resourceName = "html/" + url.toString().split("//")[1].replace("/", "-");
					try(InputStream is = TestModLoader.class.getClassLoader().getResourceAsStream(resourceName)){
						Document doc = Jsoup.parse(is, null, url.toString());
						return doc;
					}
				}
				
			},
			new JsonLoader(){
				@Override
				protected JsonElement loadPage(String pageId, URL url) throws IOException {
					String resourceName = String.format("json/%s.json", pageId);
					return super.loadPage(pageId, this.getClass().getClassLoader().getResource(resourceName));
				}
			}
		);
	}
	
	public static TinkerConfig newConfig(){
		return new TinkerConfig(null) {
			
			private final Path modsListPath = UnitTestSuite.getTempFile("mods", ".json");
			
			@Override
			public Path getGameDataPath(){
				return Paths.get("/");
			}
			
			@Override
			public Path getModsZipPath(){
				return Paths.get("zips");
			}
			
			public Path getModsListPath(){
				return modsListPath;
			}
		};
	}

}
