package aohara.tinkertime.crawlers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import aohara.tinkertime.crawlers.pageLoaders.PageLoader;

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
	public Document getPage(URL url) throws IOException {
		if (!url.getPath().endsWith("/releases")){
			url = new URL(url.toString() + "/releases");
		}
		return super.getPage(url);
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

	@Override
	public URL getImageUrl() throws IOException {
		return new URL(getLatestReleaseElement().select("img.avatar").first().attr("src"));
	}

	@Override
	public String getName() throws IOException {
		return getPage(url).select("h1.entry-title strong > a").text();
	}

	@Override
	protected String getCreator() throws IOException {
		return getLatestReleaseElement().select(" p.release-authorship a").first().text();
	}

	@Override
	public String getSupportedVersion() throws IOException {
		return null;  // Not Supported by Github
	}

	@Override
	public String generateId() {
		try { // Chop off releases from path
			URI uri = url.toURI();
			if (url.getPath().endsWith("releases")){
				uri = uri.getPath().endsWith("/") ? uri.resolve("..") : uri.resolve(".");
			}
			String[] names = uri.getPath().split("/");
			return names[names.length - 1];
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}	
	}
}
