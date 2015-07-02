package io.andrewohara.tinkertime.io.crawlers;

import io.andrewohara.tinkertime.io.crawlers.pageLoaders.PageLoader;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class KerbalStuffCrawler extends Crawler<JsonElement>{

	private static Pattern ID_PATTERN = Pattern.compile("(mod/)(\\d+)(/*)");
	private URL cachedApiUrl = null;

	public KerbalStuffCrawler(Mod mod, PageLoader<JsonElement> pageLoader) {
		super(mod, pageLoader);
	}

	@Override
	protected URL getApiUrl() throws MalformedURLException {
		if (cachedApiUrl == null){
			try {
				// Get Id from path
				Matcher m = ID_PATTERN.matcher(getPageUrl().getPath());
				if (!m.find()){
					throw new MalformedURLException();
				}

				// Construct API URL
				cachedApiUrl = new URL("https", CrawlerFactory.HOST_KERBAL_STUFF, String.format("/api/mod/%s", m.group(2)));
			} catch (IOException e) {
				throw new MalformedURLException(e.getMessage());
			}
		}
		return cachedApiUrl;
	}

	@Override
	public String getName() throws IOException {
		return getPage(getApiUrl()).getAsJsonObject().get("name").getAsString();
	}

	@Override
	public URL getImageUrl() throws IOException {
		JsonElement bgElement = getPage(getApiUrl()).getAsJsonObject().get("background");
		if (!bgElement.isJsonNull()){
			return new URL("https", getApiUrl().getHost(), bgElement.getAsString());
		}
		return null;
	}

	@Override
	public Date getUpdatedOn() throws IOException {
		return null;  // Not Available
	}

	@Override
	public String getCreator() throws IOException {
		return getPage(getApiUrl()).getAsJsonObject().get("author").getAsString();
	}

	@Override
	public String getKspVersion() throws IOException {
		return getLatestVersion().get("ksp_version").getAsString();
	}

	@Override
	public String getVersionString() throws IOException{
		return getLatestVersion().get("friendly_version").getAsString();
	}

	@Override
	protected Collection<Asset> getNewestAssets() throws IOException {
		String fileName = String.format(
				"%s %s.zip", getName(),
				getLatestVersion().get("friendly_version").getAsString()
				);

		URL downloadLink = new URL(
				"https",
				CrawlerFactory.HOST_KERBAL_STUFF,
				getLatestVersion().get("download_path").getAsString()
				);

		Collection<Asset> assets = new LinkedList<>();
		assets.add(new Asset(fileName, downloadLink));
		return assets;
	}

	/////////////
	// Helpers //
	/////////////

	private JsonObject getLatestVersion() throws IOException {
		JsonArray versions = getPage(getApiUrl()).getAsJsonObject().get("versions").getAsJsonArray();
		if (versions.size() > 0){
			return versions.get(0).getAsJsonObject();
		}
		throw new IOException("No latest version available");
	}
}
