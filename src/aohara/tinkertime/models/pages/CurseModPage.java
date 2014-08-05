package aohara.tinkertime.models.pages;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;

public class CurseModPage extends ModPage {
	
	private static Pattern ID_PATTERN = Pattern.compile("(\\d{4})(\\d{3})");
	
	public CurseModPage(Element doc, URL pageUrl){
		super(pageUrl, doc);
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
	public String getNewestFileName(){
		Element ele = doc.getElementById("project-overview");
		ele = ele.getElementsContainingOwnText("Newest File").first();
		return ele.text().split(":")[1].trim();
	}
	
	@Override
	public URL getDownloadLink() {
		Element ele = doc.select("#tab-other-downloads tr.even a").first();
		Matcher m = ID_PATTERN.matcher(ele.attr("href"));
		
		// Get Mod ids
		m.find();
		int id1 = Integer.parseInt(m.group(1));
		int id2 = Integer.parseInt(m.group(2));
		
		URL url = null;
		try {
			String urlString = String.format(
				"http://addons.curse.cursecdn.com/files/%s/%s/%s",
				id1, id2,
				getNewestFileName().replaceAll(" ", "%20")
			);
			
			url = testUrl(urlString);
			if (url == null){
				url = testUrl(urlString.replaceAll("_", "%20"));
			}			
			
		} catch (IOException | IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		return url;
	}
	
	private URL testUrl(String urlString) throws IOException{
		URL url = new URL(urlString);
		if (url.openConnection().getContentLength() > 0){
			return url;
		}
		return null;
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
}
