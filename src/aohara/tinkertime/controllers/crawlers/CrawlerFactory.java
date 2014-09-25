package aohara.tinkertime.controllers.crawlers;

import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.nodes.Document;

import com.google.gson.JsonObject;

import aohara.tinkertime.controllers.crawlers.pageLoaders.JsonLoader;
import aohara.tinkertime.controllers.crawlers.pageLoaders.PageLoader;
import aohara.tinkertime.controllers.crawlers.pageLoaders.WebpageLoader;

/**
 * Factory for creating crawlers.
 * 
 * @author Andrew O'Hara
 *
 */
public class CrawlerFactory {
	
	/**
	 * Creates a ModCrawler based on the given URL.
	 * 
	 * An Unsupported host name will throw an Exception.
	 * 
	 * @param url url of the page to be crawled
	 * @return A crawler for crawling the file data on the given url
	 */
	public ModCrawler<?> getModCrawler(URL url) throws UnsupportedHostException{
		String host = url.getHost().toLowerCase();
		String path = url.getPath().toLowerCase();
		
		if (host.equals(Constants.HOST_CURSE)){
			return new CurseCrawler(url, createHtmlLoader());
		} else if (host.equals(Constants.HOST_GITHUB)){
			if (host.equals(Constants.HOST_GITHUB) && !path.endsWith("/releases")){
				try {
					url = new URL(url.toString() + "/releases");
				} catch (MalformedURLException e) {
					throw new RuntimeException(e);
				}
			}
			return new GithubCrawler(url, createHtmlLoader());
		}
		throw new UnsupportedHostException();
	}
	
	public Crawler<?> getCrawler(URL url) throws UnsupportedHostException{
		try {
			return getModCrawler(url);
		} catch (UnsupportedHostException e){}
		
		String host = url.getHost();
		if (host.equals(Constants.HOST_MODULE_MANAGER)){
			try {
				URL artifactUrl = new URL(Constants.MODULE_MANAGER_ARTIFACT_DL_URL);
				return new JenkinsCrawler(url, createJsonLoader(), artifactUrl);
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}
		throw new UnsupportedHostException();
	}
	
	protected PageLoader<Document> createHtmlLoader(){
		return new WebpageLoader();
	}
	
	protected PageLoader<JsonObject> createJsonLoader(){
		return new JsonLoader();
	}
	
	@SuppressWarnings("serial")
	public static class UnsupportedHostException extends Exception {}
}
