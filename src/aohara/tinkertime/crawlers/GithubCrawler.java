package aohara.tinkertime.crawlers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import aohara.tinkertime.crawlers.pageLoaders.PageLoader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GithubCrawler extends Crawler<JsonElement> {

	public GithubCrawler(URL url, PageLoader<JsonElement> pageLoader) {
		super(url, pageLoader);
	}
	
	@Override
	public URL getApiUrl() throws MalformedURLException{
		String pagePath = pageUrl.getPath();
		if (pagePath.contains("/releases")){
			pagePath = pagePath.split("/releases")[0];
		}
		return new URL("https", "api.github.com", "/repos" + pagePath);
	}
	
	private JsonObject getLatestRelease() throws IOException{
		URL releasesUrl = new URL("https", "api.github.com", getApiUrl().getPath() + "/releases");
		for (JsonElement releaseEle : getPage(releasesUrl).getAsJsonArray()){
			JsonObject releaseObj = releaseEle.getAsJsonObject();
			JsonArray assets = releaseObj.get("assets").getAsJsonArray();
			if (! releaseObj.get("prerelease").getAsBoolean() && assets.size() > 0){
				return releaseObj;
			}
		}
		throw new IOException("No Releases found for " + getName());
	}
	
	private JsonObject getRepoDoc() throws IOException {
		return getPage(getApiUrl()).getAsJsonObject();
	}

	@Override
	public Date getUpdatedOn() throws IOException {
		String dateStr = getLatestRelease().get("published_at").getAsString();
  		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
		} catch (ParseException e) {
			throw new IOException(e);
		}
	}

	@Override
	public URL getImageUrl() throws IOException {
		return null;
	}

	@Override
	public String getName() throws IOException {
		return getRepoDoc().getAsJsonObject().get("name").getAsString();
	}

	@Override
	public String getCreator() throws IOException {
		return getRepoDoc().get("owner").getAsJsonObject().get("login").getAsString();
	}

	@Override
	public String getKspVersion() throws IOException {
		return null;  // Not Supported by Github
	}

	@Override
	public String getVersionString() throws IOException {
		return getLatestRelease().get("tag_name").getAsString();
	}

	@Override
	protected Collection<Asset> getNewestAssets() throws IOException {
		Collection<Asset> assets = new LinkedList<>();
		
		for (JsonElement assetEle : getLatestRelease().get("assets").getAsJsonArray()){
			JsonObject assetObj = assetEle.getAsJsonObject();
			assets.add(new Asset(
				assetObj.get("name").getAsString(),
				new URL(assetObj.get("browser_download_url").getAsString())
			));
		}
		return assets;
	}

}
