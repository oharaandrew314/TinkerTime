package aohara.tinkertime.models;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ModPage extends ModApi {
	
	private static Pattern ID_PATTERN = Pattern.compile("(\\d{4})(\\d{3})");
	
	private final Document doc;
	
	public ModPage(String url) throws IOException {
		this(Jsoup.connect(url).get());
	}
	
	public ModPage(URL url) throws IOException {
		this(url.toString());
	}
	
	public ModPage(Document doc){
		this.doc = doc;
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
		return ele.text().split(":")[1].trim();
	}
	
	@Override
	public String getNewestFile(){
		Element ele = doc.getElementById("project-overview");
		ele = ele.getElementsContainingOwnText("Newest File").first();
		return ele.text().split(":")[1].trim();
	}
	
	@Override
	public URL getDownloadLink(){
		Element ele = doc.select("#tab-other-downloads tr.even a").first();
		Matcher m = ID_PATTERN.matcher(ele.attr("href"));

		m.find();
		try {
			return new URL(String.format(
				"http://addons.curse.cursecdn.com/files/%s/%s/%s",
				m.group(1), m.group(2),
				getNewestFile().replaceAll("_", " ").replaceAll(" ", "%20")
			));
		} catch (MalformedURLException | IndexOutOfBoundsException e) {
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
			return new URL(doc.location());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("serial")
	public class CannotScrapeException extends Throwable {}
}
