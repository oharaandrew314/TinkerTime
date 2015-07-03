package io.andrewohara.tinkertime.io.crawlers;

import io.andrewohara.tinkertime.io.crawlers.pageLoaders.PageLoader;
import io.andrewohara.tinkertime.models.mod.Mod;

import org.jsoup.nodes.Document;

import com.google.gson.JsonElement;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class CrawlerFactory {

	public static final String
	HOST_CURSE = "curse.com",
	HOST_GITHUB = "github.com",
	HOST_MODULE_MANAGER = "ksp.sarbian.com",
	HOST_KERBAL_STUFF = "kerbalstuff.com";

	public static final String[] ACCEPTED_MOD_HOSTS	 = new String[]{
		HOST_KERBAL_STUFF, HOST_CURSE, HOST_GITHUB
	};

	private final PageLoader<Document> docLoader;
	private final PageLoader<JsonElement> jsonLoader;

	@Inject
	CrawlerFactory(PageLoader<Document> docLoader, PageLoader<JsonElement> jsonLoader){
		this.docLoader = docLoader;
		this.jsonLoader = jsonLoader;
	}

	public Crawler<?> getCrawler(Mod mod) throws UnsupportedHostException{
		String host = mod.getUrl().getHost();
		if (host.contains(HOST_CURSE)){
			return new CurseCrawler(mod, docLoader);
		} else if (host.contains(HOST_GITHUB)){
			return new GithubCrawler(mod, jsonLoader);
		} else if (host.contains(HOST_KERBAL_STUFF)){
			return new KerbalStuffCrawler(mod, jsonLoader);
		} else if (host.equals(HOST_MODULE_MANAGER)){
			return new JenkinsCrawler(mod, jsonLoader);
		}
		throw new UnsupportedHostException(host);
	}

	@SuppressWarnings("serial")
	public static class UnsupportedHostException extends Exception {

		private UnsupportedHostException(String host){
			super(String.format("Unsupported host: %s", host));
		}
	}
}
