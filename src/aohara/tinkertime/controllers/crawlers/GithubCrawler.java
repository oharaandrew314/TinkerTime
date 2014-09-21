package aohara.tinkertime.controllers.crawlers;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import aohara.tinkertime.controllers.crawlers.pageLoaders.PageLoader;
import aohara.tinkertime.models.Mod;

/**
 * Crawler for gathering Mod Data from a Github Project.
 * 
 * @author Andrew O'Hara
 */
public class GithubCrawler extends ModCrawler<Document> {

	public GithubCrawler(URL url, PageLoader<Document> pageLoader) {
		super(url, pageLoader);
	}

	@Override
	public Mod createMod() throws IOException {
		Document doc = getPage(url);

		String name = doc.select("h1.entry-title strong > a").text();
		String creator = getLatestReleaseElement().select(" p.release-authorship a").first().text();
		URL imageUrl = new URL(getLatestReleaseElement().select("img.avatar").first().attr("src"));
		
		return new Mod(name, getNewestFileName(), creator, imageUrl, url, getUpdatedOn());
	}
	
	private Element getLatestReleaseElement() throws IOException {
		Document doc = getPage(url);
		return doc.select("div.label-latest").first();
	}
	
	private Element getDownloadElement() throws IOException {
		return getLatestReleaseElement().select("div.release-body ul.release-downloads a").first();
	}

	@Override
	public URL getDownloadLink() throws IOException {
		return new URL(getDownloadElement().absUrl("href"));
	}

	@Override
	public String getNewestFileName() throws IOException {
		return getDownloadElement().text();
	}

	@Override
	protected Date getUpdatedOn() throws IOException {
		Element dateElement = getLatestReleaseElement().select("p.release-authorship time").first();
		String dateStr = dateElement.attr("datetime");
		
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
		} catch (ParseException e) {
			throw new IOException(e);
		}
	}

}
