package aohara.tinkertime.testutil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.crawlers.CrawlerFactory;
import aohara.tinkertime.crawlers.pageLoaders.JsonLoader;
import aohara.tinkertime.crawlers.pageLoaders.PageLoader;
import aohara.tinkertime.models.Mod;

import com.google.gson.JsonElement;

public class MockHelper {
	
	private static String urlToPath(URL url){
		return url.toString().split("://")[1].replace("/", "-");
	}
	
	public static CrawlerFactory newCrawlerFactory(){
		CrawlerFactory factory = new CrawlerFactory(
			new PageLoader<Document>(){
				@Override
				protected Document loadPage(URL url) throws IOException {
					String resourceName = "html/" + urlToPath(url);
					try(InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName)){
						return Jsoup.parse(is, null, url.toString());
					}
				}
				
			},
			new JsonLoader(){
				@Override
				protected JsonElement loadPage(URL url) throws IOException {
					String resourceName = "json/" + urlToPath(url);
					URL resourceUrl = getClass().getClassLoader().getResource(resourceName);
					return super.loadPage(resourceUrl);
				}
			}
		);
		factory.setFallbacksEnabled(false);
		return factory;
	}
	
	public static TinkerConfig newConfig(){
		return new TinkerConfig(null) {
			
			private Path modsListPath;
			
			@Override
			public Path getGameDataPath(){
				return Paths.get("/");
			}
			
			@Override
			public Path getModsZipPath(){
				return Paths.get("zips");
			}
			
			public Path getModsListPath(){
				if (modsListPath == null){
					try {
						modsListPath = Files.createTempFile("mods", ".json");
					} catch (IOException e) {
						e.printStackTrace();
					}
					modsListPath.toFile().deleteOnExit();
				}
				
				return modsListPath;
			}
			
			public int numConcurrentDownloads(){
				return 4;
			}
		};
	}
	
	public static class MockMod extends Mod {
			
		private boolean downloaded = false;
		private boolean enabled = false;

		public MockMod(Mod mod) {
			super(
				mod.id,
				mod.name,
				mod.newestFileName,
				mod.creator,
				mod.pageUrl,
				mod.updatedOn,
				mod.supportedVersion
			);
		}
		
		public void setDownloaded(boolean downloaded){
			this.downloaded = downloaded;
		}
		
		@Override
		public Path getCachedZipPath(TinkerConfig config){
			return downloaded ? TestModLoader.getZipPath(name) : Paths.get("/");
		}
		
		@Override
		public boolean isEnabled(TinkerConfig config){
			return enabled;
		}
		
		public boolean isEnabled(){
			return isEnabled(null);
		}
		
		public void setEnabled(boolean enabled){
			this.enabled = enabled;
		}
	}
}
