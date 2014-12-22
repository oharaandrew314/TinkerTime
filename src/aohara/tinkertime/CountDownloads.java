package aohara.tinkertime;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * I just made this because it's exciting watching the download counts rise.
 * 
 * @author Andrew O'Hara
 */
public class CountDownloads {
	
	private static final JsonParser parser = new JsonParser();
	
	private static final String
		GITHUB_URL = "https://api.github.com/repos/oharaandrew314/TinkerTime/releases",
		KS_URL = "https://kerbalstuff.com/api/mod/243";
	
	private static final JsonElement getJson(String url) throws JsonIOException, JsonSyntaxException, MalformedURLException, IOException{
		return parser.parse(new InputStreamReader(new URL(url).openStream()));
	}
	
	public static void main(String[] args) throws Exception {
		// Count Github Downloads
		int ghDls = 0;
		for (JsonElement release : getJson(GITHUB_URL).getAsJsonArray()){
			for (JsonElement asset : release.getAsJsonObject().get("assets").getAsJsonArray()){
				ghDls += asset.getAsJsonObject().get("download_count").getAsInt();
			}
		}
		
		// Count KerbalStuff Downloads
		int ksDls = getJson(KS_URL).getAsJsonObject().get("downloads").getAsInt();
		
		System.out.println(ghDls + " downloads from Github");
		System.out.println(ksDls + " downloads from KerbalStuff");
		System.out.println((ghDls + ksDls) + " downloads in total");
	}
}
