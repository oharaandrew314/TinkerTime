package aohara.tinkertime.models;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ModPage implements ModApi {
	
	private final Document doc;
	private final String url;
	
	public ModPage(String url) throws IOException {
		this.url = url;
		doc = Jsoup.connect(url).get();
	}
	
	@Override
	public String getName(){
		Element ele = doc.getElementById("project-overview");
		ele = ele.getElementsByClass("caption").first();
		return ele.text();
	}
	
	@Override
	public Date getUpdatedOn(){
		Element ele = doc.getElementById("project-overview");
		ele = ele.getElementsContainingOwnText("Updated").first();
		String dateText = ele.text().replace("Updated", "").trim();
		
		try {
			return new SimpleDateFormat("MM/dd/yyyy").parse(dateText);
		} catch (ParseException e) {
			return null;
		}
	}
	
	@Override
	public String getCreator(){
		Element ele = doc.getElementById("project-overview");
		ele = ele.getElementsContainingOwnText("Manager").first();
		return ele.text();
	}
	
	@Override
	public String getNewestFile(){
		Element ele = doc.getElementById("project-overview");
		ele = ele.getElementsContainingOwnText("Newest File").first();
		return ele.text().split(":")[1].trim();
	}
	
	@Override
	public URL getDownloadLink(){
		Element ele = doc.select(
			"#tab-other-downloads > div > div.listing-body" +
			"> table > tbody > tr.even > td:nth-child(1) > a"
		).first();
		
		String[] bits = ele.attr("href").split("/");
		String id = bits[bits.length - 1];
		
		try {
			return new URL(String.format(
				"http://addons.curse.cursecdn.com/files/%s/%s/%s",
				id.substring(0, 4),
				id.substring(4),
				getNewestFile().replaceAll("_", " ").replaceAll(" ", "%20")
			));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public URL getImageUrl(){
		try {
			Element ele = doc.select("img.primary-project-attachment").first();
			return new URL(ele.attr("src"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public URL getPageUrl(){
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
