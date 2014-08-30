package aohara.tinkertime.controllers.crawlers;

import java.net.MalformedURLException;
import java.net.URL;

import aohara.tinkertime.Constants;
import aohara.tinkertime.controllers.crawlers.pageLoaders.JsonLoader;
import aohara.tinkertime.controllers.crawlers.pageLoaders.WebpageLoader;

public class CrawlerFactory {
	
	public Crawler<?, ?> getCrawler(URL url){
		
		String host = url.getHost();
		if (host.equals(Constants.HOST_CURSE)){
			return new CurseCrawler(url, new WebpageLoader());
		} else if (host.equals(Constants.HOST_GITHUB)){
			return new GithubCrawler(url, new WebpageLoader());
		} else if (host.equals(Constants.HOST_MODULE_MANAGER)){
			try {
				URL artifactUrl = new URL(Constants.MODULE_MANAGER_ARTIFACT_DL_URL);
				return new JenkinsCrawler(url, new JsonLoader(), artifactUrl);
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}
		throw new UnsupportedOperationException();
	}
}
