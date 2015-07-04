package io.andrewohara.tinkertime.io.crawlers;

import io.andrewohara.tinkertime.io.crawlers.pageLoaders.PageLoader;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Crawler for gethering Mod File Data from curse.com
 *
 * @author Andrew O'Hara
 */
public class CurseCrawler extends Crawler<Document> {

	public CurseCrawler(URL url, PageLoader<Document> pageLoader){
		super(url, pageLoader);
	}

	@Override
	public Date getUpdatedOn() throws IOException {
		Document mainPage = getPage(getApiUrl());
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
	public URL getImageUrl() throws IOException {
		Document mainPage = getPage(getApiUrl());
		Element ele = mainPage.select("img.primary-project-attachment").first();
		return new URL(ele.absUrl("src"));
	}

	@Override
	public String getName() throws IOException {
		Element ele = getPage(getApiUrl()).getElementById("project-overview");
		ele = ele.getElementsByClass("caption").first();
		return ele.text();
	}

	@Override
	public String getCreator() throws IOException {
		Element ele = getPage(getApiUrl()).getElementById("project-overview");
		ele = ele.getElementsContainingOwnText("Manager").first();
		return ele.text().split(":")[1].trim();
	}

	@Override
	public String getKspVersion() throws IOException {
		String text = getPage(getApiUrl()).select("li.version").first().text();
		return text.split(":")[1].trim();
	}

	@Override
	protected Collection<Asset> getNewestAssets() throws IOException {
		Document mainPage = getPage(getApiUrl());

		// Get File Name From Main Page
		Element ele = mainPage.getElementById("project-overview");
		ele = ele.getElementsContainingOwnText("Newest File").first();

		// Add zip extension to filename if author did not add it
		String fileName = ele.text().split(":")[1].trim();
		if (!fileName.toLowerCase().endsWith(".zip")){
			fileName += ".zip";
		}

		// Get Download Page Link
		String downloadPageLink = mainPage.select("ul.regular-dl a").first().absUrl("href");
		URL downloadPageUrl = new URL(downloadPageLink);

		// Get Mod Download Link from Download Page
		Document downloadPage = getPage(downloadPageUrl);
		String downloadLink = downloadPage.select("a.download-link").first().absUrl("data-href");
		URL downloadUrl = new URL(downloadLink.replace(" ", "%20"));

		// Return Asset
		Collection<Asset> assets = new LinkedList<>();
		assets.add(new Asset(fileName, downloadUrl));
		return assets;
	}

	@Override
	public String getVersionString() throws IOException {
		return null;
	}
}
