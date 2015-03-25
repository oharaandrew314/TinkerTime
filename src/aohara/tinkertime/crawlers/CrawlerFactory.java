package aohara.tinkertime.crawlers;

import java.io.IOException;
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
	
	static final String
		HOST_CURSE = "curse.com",
		HOST_GITHUB = "github.com",
		HOST_MODULE_MANAGER = "ksp.sarbian.com",
		HOST_KERBAL_STUFF = "kerbalstuff.com";

	public static final String[] ACCEPTED_MOD_HOSTS	 = new String[]{
		HOST_CURSE, HOST_GITHUB, HOST_KERBAL_STUFF
	};
	
	private final PageLoader<Document> htmlLoader;
	private final PageLoader<JsonElement> jsonLoader;
	private boolean fallbacksEnabled = true;
	
	public CrawlerFactory(PageLoader<Document> htmlLoader, PageLoader<JsonElement> jsonLoader){
		this.htmlLoader = htmlLoader;
		this.jsonLoader = jsonLoader;
	}
	
	public void setFallbacksEnabled(boolean enabled){
		this.fallbacksEnabled = enabled;
	}
	
	public static URL getModuleManagerUrl(){
		try {
			return new URL("https", HOST_MODULE_MANAGER, "/jenkins/job/ModuleManager");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e); // Programming error if this occurs
		}
	}
	
	public Crawler<?> getCrawler(URL url) throws UnsupportedHostException{
		return getCrawler(url, false);
	}
	
	public Crawler<?> getCrawler(URL url, boolean fallback) throws UnsupportedHostException{
		String host = url.getHost();
		
		if (host.contains(HOST_CURSE)){
			return new CurseCrawler(url, htmlLoader);
		} else if (host.contains(HOST_GITHUB)){
			if (!fallback || !fallbacksEnabled){
				try {
					Crawler<?> crawler = new GithubJsonCrawler(url, jsonLoader);
					if (fallbacksEnabled){
						crawler.getPage(crawler.getApiUrl());  // Test connection; throw IOException if failure
					}
					return crawler;
				} catch (IOException e) {
					return getCrawler(url, true);
				}
			}
			return new GithubHtmlCrawler(url, htmlLoader);
		} else if (host.contains(HOST_KERBAL_STUFF)){
			return new KerbalStuffCrawler(url, jsonLoader);
		} else if (host.equals(HOST_MODULE_MANAGER)){
			try {
				return new JenkinsCrawler(url, jsonLoader);
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
				+ " Valid hosts are: " + Arrays.asList(ACCEPTED_MOD_HOSTS)
			);
		}
	}
}
