package aohara.tinkertime.crawlers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import aohara.tinkertime.crawlers.pageLoaders.PageLoader;

/**
 * Crawler for gathering Mod Data from a Github Project.
 * 
 * @author Andrew O'Hara
 */
public class GithubHtmlCrawler extends Crawler<Document> {
	
	private static final String RELEASES = "releases";
	
	public GithubHtmlCrawler(URL url, PageLoader<Document> pageLoader) {
		super(url, pageLoader);
	}
	
	@Override
	public URL getApiUrl() throws MalformedURLException {
		URL url = getPageUrl();
		
		/* Groom the Github Releases URL before getting it */
		List<String> paths = Arrays.asList(url.getPath().split("/"));
		if (paths.contains(RELEASES)){
			// If path contains releases, but isn't last element, strip url
			if (!paths.get(paths.size() - 1).equals(RELEASES)){
				return new URL(url.toString().split(RELEASES)[0]);
			}
			// Otherwise, releases is in correct spot
			return url;
		}
		
		// If path does not contain releases, try appending it to end 
		return new URL(String.format("%s/%s", url, RELEASES));
	}
	
	private Element getLatestReleaseElement() throws IOException {
		Document doc = getPage(getApiUrl());
		
		// For all the releases, get the latest one that has user-uploaded releases
		for (Element releaseElement : doc.select("div[class~=release label]")){
			
			// Skip pre-releases
			if (releaseElement.classNames().contains("label-prerelease")){
				continue;
			}
			
			for (Element assetLink : getAssetLinks(releaseElement)){
				if (isUserAssetLink(assetLink)){
					return releaseElement;
				}
				
			}
		}
		return null;
	}
	
	private boolean isUserAssetLink(Element element){
		return element.html().contains("octicon-package");
	}
	
	private Elements getAssetLinks(Element element){
		return element.select("ul.release-downloads li a");
	}

	@Override
	protected Collection<Asset> getNewestAssets() throws IOException {
		Collection<Asset> assets = new LinkedList<>();
		
		Element releaseElement  = getLatestReleaseElement();
		if (releaseElement == null){
			return assets;
		}
		
		for (Element assetLink : getAssetLinks(releaseElement)){
			if (isUserAssetLink(assetLink)){
				assets.add(new Asset(
					assetLink.attr("href").substring(assetLink.attr("href").lastIndexOf('/') + 1),
					new URL(assetLink.absUrl("href"))
				));
			}
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
	public String getKspVersion() throws IOException {
		return null;  // Not Supported by Github
	}

	@Override
	public String getVersionString() throws IOException {
		return getLatestReleaseElement().select("span.css-truncate-target").text();
	}
}