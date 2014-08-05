package aohara.tinkertime.models.pages;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.nodes.Element;

import aohara.tinkertime.Constants;

public class GithubModPage extends ModPage {
	
	public GithubModPage(Element doc, URL pageUrl){
		super(pageUrl, doc);
	}
	
	private Element getLatestReleaseElement(){
		return doc.select("div.label-latest").first();
	}
	
	private Element getDownloadElement(){
		return getLatestReleaseElement().select("div.release-body ul.release-downloads a").first();
	}

	@Override
	public Date getUpdatedOn() {
		Element dateElement = getLatestReleaseElement().select("p.release-authorship time").first();
		String dateStr = dateElement.attr("datetime");
		
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
		} catch (ParseException e) {
			return null;
		}
	}

	@Override
	public String getNewestFileName() {
		return getDownloadElement().text();
	}

	@Override
	public URL getDownloadLink() {
		try {
			return new URL(
				new URL("https://" + Constants.GITHUB_HOST),
				getDownloadElement().attr("href")
			);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean isUpdateAvailable(Date lastUpdated) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		return doc.select("h1.entry-title strong > a").text();
	}

	@Override
	public String getCreator() {
		return getLatestReleaseElement().select(" p.release-authorship a").first().text();
	}

	@Override
	public URL getImageUrl() {
		try {
			return new URL(getLatestReleaseElement().select("span.avatar > img").first().attr("src"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

}
