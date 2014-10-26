package aohara.tinkertime.crawlers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.jsoup.nodes.Document;

import com.google.gson.JsonObject;

import aohara.tinkertime.crawlers.pageLoaders.JsonLoader;
import aohara.tinkertime.crawlers.pageLoaders.PageLoader;
import aohara.tinkertime.crawlers.pageLoaders.WebpageLoader;

/**
 * Factory for creating crawlers.
 * 
 * @author Andrew O'Hara
 *
 */
public class CrawlerFactory {
	
	public Crawler<?> getCrawler(URL url) throws UnsupportedHostException{
		String host = url.getHost();
		
		if (host.contains(Constants.HOST_CURSE)){
			return new CurseCrawler(url, createHtmlLoader());
		} else if (host.contains(Constants.HOST_GITHUB)){
			return new GithubCrawler(url, createHtmlLoader());
		} else if (host.contains(Constants.HOST_KERBAL_STUFF)){
			return new KerbalStuffCrawler(url, createJsonLoader());
		} else if (host.equals(Constants.HOST_MODULE_MANAGER)){
			try {
				URL artifactUrl = new URL(Constants.MODULE_MANAGER_ARTIFACT_DL_URL);
				return new JenkinsCrawler(url, createJsonLoader(), "Module Manager", artifactUrl);
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}
		
		throw new UnsupportedHostException(host);
	}
	
	protected PageLoader<Document> createHtmlLoader(){
		return new WebpageLoader();
	}
	
	protected PageLoader<JsonObject> createJsonLoader(){
		return new JsonLoader();
	}
	
	@SuppressWarnings("serial")
	public static class UnsupportedHostException extends Exception {
		
		private final String host;
		
		public UnsupportedHostException(String host){
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
