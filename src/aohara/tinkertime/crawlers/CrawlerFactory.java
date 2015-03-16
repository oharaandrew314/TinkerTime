package aohara.tinkertime.crawlers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.jsoup.nodes.Document;

import aohara.tinkertime.crawlers.pageLoaders.PageLoader;

import com.google.gson.JsonElement;

/**
 * Factory for creating crawlers.
 * 
 * @author Andrew O'Hara
 *
 */
public class CrawlerFactory {
	
	private final PageLoader<Document> htmlLoader;
	private final PageLoader<JsonElement> jsonLoader;
	
	public CrawlerFactory(PageLoader<Document> htmlLoader, PageLoader<JsonElement> jsonLoader){
		this.htmlLoader = htmlLoader;
		this.jsonLoader = jsonLoader;
	}
	
	public Crawler<?> getCrawler(URL url) throws UnsupportedHostException{
		String host = url.getHost();
		
		if (host.contains(Constants.HOST_CURSE)){
			return new CurseCrawler(url, htmlLoader);
		} else if (host.contains(Constants.HOST_GITHUB)){
			return new GithubCrawler(url, jsonLoader);
		} else if (host.contains(Constants.HOST_KERBAL_STUFF)){
			return new KerbalStuffCrawler(url, jsonLoader);
		} else if (host.equals(Constants.HOST_MODULE_MANAGER)){
			try {
				URL artifactUrl = new URL(Constants.MODULE_MANAGER_ARTIFACT_DL_URL);
				return new JenkinsCrawler(url, jsonLoader, "Module Manager", artifactUrl);
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}
		
		throw new UnsupportedHostException(host);
	}
	
	@SuppressWarnings("serial")
	public static class UnsupportedHostException extends Exception {
		
		private final String host;
		
		private UnsupportedHostException(String host){
			this.host = host;
		}
		
		@Override
		public String getMessage(){
			return (
				"Mod data could not be deciphered for " + host + ".\n"
				+ "Either the URL is invalid, or the site layout has been updated.\n"
				+ " Valid hosts are: " + Arrays.asList(Constants.ACCEPTED_MOD_HOSTS)
			);
		}
	}
}
