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
 * Crawler for gethering Mod File Data from curse.com
 * 
 * @author Andrew O'Hara
 */
public class CurseCrawler extends ModCrawler<Document> {
	
	public CurseCrawler(URL url, PageLoader<Document> pageLoader){
		super(url, pageLoader);
	}

	/**
	 * Crawls the page and returns a Mod model with all of the Mod's current data.
	 * 
	 * @return Mod representing the mod's most recent data
	 */
	@Override
	public Mod createMod() throws IOException {
		// Creator
		Element ele = getPage(url).getElementById("project-overview");
		ele = ele.getElementsContainingOwnText("Manager").first();
		String creator = ele.text().split(":")[1].trim();

		return new Mod(getName(), getNewestFileName(), creator, getImageUrl(), url, getUpdatedOn());
	}
	
	@Override
	protected Date getUpdatedOn() throws IOException {
		Document mainPage = getPage(url);
		Element ele = mainPage.getElementById("project-overview");
		ele = ele.getElementsContainingOwnText("Updated").first();
		String dateText = ele.text().replace("Updated", "").trim();
		try {
			return new SimpleDateFormat("MM/dd/yyyy").parse(dateText);
		} catch (ParseException e) {
			throw new IOException(e.getMessage());
		}
	}
	
	@Override
	public String getNewestFileName() throws IOException{
		Document mainPage = getPage(url);
		Element ele = mainPage.getElementById("project-overview");
		ele = ele.getElementsContainingOwnText("Newest File").first();
		return ele.text().split(":")[1].trim();
	}
	
	@Override
	public URL getDownloadLink() throws IOException{
		// Get Download Page Link
		Document mainPage = getPage(url);
		String downloadPageLink = mainPage.select("ul.regular-dl a").first().absUrl("href");
		URL downloadPageUrl = new URL(downloadPageLink);
		
		// Get Mod Download Link from Download Page
		Document downloadPage = getPage(downloadPageUrl);
		String downloadLink = downloadPage.select("a.download-link").first().absUrl("data-href");
		return new URL(downloadLink.replace(" ", "%20"));
	}

	@Override
	public URL getImageUrl() throws IOException {
		Document mainPage = getPage(url);
		Element ele = mainPage.select("img.primary-project-attachment").first();
		return new URL(ele.absUrl("src"));
	}

	@Override
	public String getName() throws IOException {
		Element ele = getPage(url).getElementById("project-overview");
		ele = ele.getElementsByClass("caption").first();
		return ele.text();
	}
}
