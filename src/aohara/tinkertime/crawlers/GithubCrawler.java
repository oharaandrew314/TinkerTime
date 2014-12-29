package aohara.tinkertime.crawlers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import aohara.tinkertime.crawlers.pageLoaders.PageLoader;

/**
 * Crawler for gathering Mod Data from a Github Project.
 * 
 * @author Andrew O'Hara
 */
public class GithubCrawler extends Crawler<Document> {
	
	private static final String RELEASES = "releases";

	//private Entry<String, URL> cachedAsset;
	
	public GithubCrawler(URL url, PageLoader<Document> pageLoader) {
		super(url, pageLoader);
	}
	
	@Override
	public Document getPage(URL url) throws IOException {
		/* Groom the Github Releases URL before getting it */
		
		List<String> paths = Arrays.asList(url.getPath().split("/"));
		if (paths.contains(RELEASES)){
			// If path contains releases, but isn't last element, strip url
			if (!paths.get(paths.size() - 1).equals(RELEASES)){
				url = new URL(url.toString().split(RELEASES)[0]);
			}
		}
		
		// If path does not contain releases, try appending it to end 
		else {
			url = new URL(url.toString() + "/releases");
		}
		
		return super.getPage(url);
	}
	
	private Element getLatestReleaseElement() throws IOException {
		Document doc = getPage(getApiUrl());
		return doc.select("div.label-latest").first();
	}

	@Override
	protected Collection<Asset> getNewestAssets() throws IOException {
		// Get List of Asset Elements
		Element assetsElement = getLatestReleaseElement().select("div.release-body ul.release-downloads").first();
		LinkedHashSet<Element> assetElements = new LinkedHashSet<>(assetsElement.select("li a.primary"));
		
		// Get File Names from Elements
		Collection<Asset> assets = new LinkedList<>();
		for (Element assetEle : assetElements){
			assets.add(new Asset(
					assetEle.attr("href").substring(assetEle.attr("href").lastIndexOf('/') + 1),
					new URL(assetEle.absUrl("href"))
			));
		}
		return assets;
	}

	@Override
	public Date getUpdatedOn() throws IOException {
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
		return null;
	}

	@Override
	public String getName() throws IOException {
		return getPage(getApiUrl()).select("h1.entry-title strong > a").text();
	}

	@Override
	public String getCreator() throws IOException {
		return getLatestReleaseElement().select(" p.release-authorship a").first().text();
	}

	@Override
	public String getSupportedVersion() throws IOException {
		return null;  // Not Supported by Github
	}

	@Override
	public String generateId() {
		try { // Chop off releases from path
			URI uri = getApiUrl().toURI();
			if (getApiUrl().getPath().endsWith("releases")){
				uri = uri.getPath().endsWith("/") ? uri.resolve("..") : uri.resolve(".");
			}
			String[] names = uri.getPath().split("/");
			return names[names.length - 1];
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}	
	}
}
