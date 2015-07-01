package io.andrewohara.tinkertime.io.crawlers;

import io.andrewohara.common.version.Version;
import io.andrewohara.common.version.VersionParser;
import io.andrewohara.tinkertime.io.crawlers.pageLoaders.PageLoader;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Crawler for gathering file data from a Jenkins Json Artifact.
 *
 * @author Andrew O'Hara
 */
public class JenkinsCrawler extends Crawler<JsonElement> {

	private JsonObject cachedJson;

	public JenkinsCrawler(Mod mod, PageLoader<JsonElement> pageLoader) {
		super(mod, pageLoader);
	}

	@Override
	public URL getApiUrl(){
		try {
			return new URL(String.format("%s/lastSuccessfulBuild/api/json", getPageUrl()));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private JsonObject getJson() throws IOException {
		if (cachedJson == null){
			cachedJson = getPage(getApiUrl()).getAsJsonObject();
		}
		return cachedJson;
	}

	@Override
	public Date getUpdatedOn() throws IOException {
		long timestamp = getJson().get("timestamp").getAsLong();

		// ignore milliseconds
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}

	@Override
	public URL getImageUrl() throws IOException {
		return null;
	}

	@Override
	public String getName() {
		String jobName = getPageUrl().getPath().split("job/")[1];
		if (jobName.contains("/")){
			jobName = jobName.split("/")[0];
		}
		return jobName;
	}

	@Override
	public String getCreator() throws IOException {
		return getPageUrl().getHost();
	}

	@Override
	public String getKspVersion() throws IOException {
		return null;
	}

	private JsonObject getLatestArtifact() throws IOException{
		Version latestVersion = null;
		JsonObject latestArtifact = null;

		for (JsonElement artifactEle : getJson().get("artifacts").getAsJsonArray()){
			JsonObject artifactObj = artifactEle.getAsJsonObject();
			String fileName = artifactObj.get("relativePath").getAsString();
			Version version = Version.valueOf(VersionParser.parseVersionString(fileName));
			if (latestVersion == null || (!latestVersion.greaterThan(version) && fileName.endsWith(".dll"))){
				latestVersion = version;
				latestArtifact = artifactObj;
			}
		}
		return latestArtifact;
	}

	@Override
	protected Collection<Asset> getNewestAssets() throws IOException {
		Collection<Asset> assets = new LinkedList<>();

		JsonObject dllArtifact = getLatestArtifact();
		String fileName = dllArtifact.get("relativePath").getAsString();

		assets.add(new Asset(
				fileName,
				new URL(String.format("%s/lastSuccessfulBuild/artifact/%s", getPageUrl(), fileName))
				));

		return assets;
	}

	@Override
	public String getVersionString() throws IOException {
		return getJson().get("number").getAsString();
	}
}
