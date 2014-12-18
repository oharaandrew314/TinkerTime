package aohara.tinkertime.crawlers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import javax.swing.JOptionPane;

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

	private String downloadAsset;
	
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
	
	private Element getDownloadElement() throws IOException {
		// Linked hash set to preserve order (So it matches the order on GitHub)
		LinkedHashSet<Element> assets = new LinkedHashSet<>(getLatestReleaseElement().select("div.release-body ul.release-downloads").first().select("li a.primary"));
		
		switch (assets.size()) {
			case 0: {
				// No non-source downloads
				throw new IOException("No releases found for this mod");
			}
			case 1: {
				// One non-source download; use it by default
				return assets.iterator().next();
			}
			default: {
				// More than one non-source download
				// Convert set to map
				HashMap<String, Element> releases = new HashMap<>();
				for (Element dl : assets){
					releases.put(dl.attr("href").substring(dl.attr("href").lastIndexOf('/') + 1), dl);
				}
				
				// Check if we already have a selected asset
				if (downloadAsset != null && releases.containsKey(downloadAsset))
					return releases.get(downloadAsset);
				
				// Ask user
				downloadAsset = (String) JOptionPane.showInputDialog(null,
						"Which variant of the mod should be used?",
						"Multiple Downloads Available",
						JOptionPane.QUESTION_MESSAGE,
						null,
						releases.keySet().toArray(),
						releases.keySet().toArray()[0]);
				
				if (downloadAsset == null)
					throw new IOException("You must select a download to use the mod!");
				return releases.get(downloadAsset);
			}	
		}
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
		return new URL(getLatestReleaseElement().select("img.avatar").first().attr("src"));
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
