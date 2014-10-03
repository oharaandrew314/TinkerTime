package aohara.tinkertime.crawlers;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import aohara.tinkertime.crawlers.pageLoaders.PageLoader;

public class KerbalStuffCrawler extends ModCrawler <JsonObject>{
	
	private static Pattern ID_PATTERN = Pattern.compile("(mod/)(\\d+)(/*)");

	public KerbalStuffCrawler(URL url, PageLoader<JsonObject> pageLoader) {
		super(getApiURL(url), pageLoader);
	}
	
	private static URL getApiURL(URL url){
		Matcher m = ID_PATTERN.matcher(url.getPath());
		if (m.find()){
			String path = String.format("/api/mod/%s", m.group(2));
			try {
				return new URL("https", Constants.HOST_KERBAL_STUFF, path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public String getName() throws IOException {
		return getPage(url).get("name").getAsString();
	}

	@Override
	public URL getImageUrl() throws IOException {
		String imagePath = getPage(url).get("background").getAsString();
		return new URL("https", "cdn.mediacru.sh", imagePath);
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
		JsonArray versions = getPage(url).get("versions").getAsJsonArray();
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
		return null;
	}

	@Override
	protected String getCreator() throws IOException {
		return getPage(url).get("author").getAsString();
	}

	@Override
	public String getSupportedVersion() throws IOException {
		return getLatestVersion().get("ksp_version").getAsString();
	}
}
