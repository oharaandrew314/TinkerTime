package aohara.tinkertime.crawlers;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import aohara.tinkertime.crawlers.pageLoaders.PageLoader;

public class KerbalStuffCrawler extends ModCrawler<JsonObject>{
	
	private static Pattern ID_PATTERN = Pattern.compile("(mod/)(\\d+)(/*)");

	public KerbalStuffCrawler(URL url, PageLoader<JsonObject> pageLoader) {
		super(url, pageLoader);
	}
	
	@Override
	public URL getApiUrl(){
		try {
			return new URL(
				"https",
				Constants.HOST_KERBAL_STUFF,
				String.format("/api/mod/%s", generateId(getPageUrl()))
			);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getName() throws IOException {
		return getPage(getApiUrl()).get("name").getAsString();
	}

	@Override
	public URL getImageUrl() throws IOException {
		JsonElement bgElement = getPage(getApiUrl()).get("background");
		if (!bgElement.isJsonNull()){
			return new URL("https", "cdn.mediacru.sh", bgElement.getAsString());
		}
		return null;
	}
	
	@Override
	public URL getDownloadLink() throws IOException {
		return new URL(
			"https",
			Constants.HOST_KERBAL_STUFF,
			getLatestVersion().get("download_path").getAsString()
		);
	}
	
	private JsonObject getLatestVersion() throws IOException {
		JsonArray versions = getPage(getApiUrl()).get("versions").getAsJsonArray();
		if (versions.size() > 0){
			return versions.get(0).getAsJsonObject();
		}
		throw new IOException("No latest version available");
	}

	@Override
	public String getNewestFileName() throws IOException {
		return String.format(
			"%s %s.zip", getName(),
			getLatestVersion().get("friendly_version").getAsString()
		);
	}

	@Override
	protected Date getUpdatedOn() throws IOException {
		return null;  // Not Available
	}

	@Override
	protected String getCreator() throws IOException {
		return getPage(getApiUrl()).get("author").getAsString();
	}

	@Override
	public String getSupportedVersion() throws IOException {
		return getLatestVersion().get("ksp_version").getAsString();
	}

	@Override
	public String generateId(){
		return generateId(getApiUrl());
	}
	
	private static String generateId(URL url) {
		Matcher m = ID_PATTERN.matcher(url.getPath());
		if (m.find()){
			return m.group(2);
		}
		return null;
	}
}
