package aohara.tinkertime.crawlers;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

import aohara.tinkertime.crawlers.pageLoaders.PageLoader;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Crawler for gathering file data from a Jenkins Json Artifact.
 * 
 * @author Andrew O'Hara
 */
public class JenkinsCrawler extends Crawler<JsonObject> {
	
	private JsonObject cachedJson;
	private final URL artifactDownloadUrl;
	
	public JenkinsCrawler(URL url, PageLoader<JsonObject> pageLoader, URL artifactDownloadUrl) {
		super(url, pageLoader);
		this.artifactDownloadUrl = artifactDownloadUrl;
	}

	private JsonObject getJson() throws IOException {
		if (cachedJson == null){
			cachedJson = getPage(url);
		}
		return cachedJson;
	}

	@Override
	public URL getDownloadLink() throws IOException {
		return new URL(artifactDownloadUrl, getNewestFileName());
	}

	@Override
	public String getNewestFileName() throws IOException {
		JsonArray artifacts = getJson().get("artifacts").getAsJsonArray();
		JsonObject dllArtifact = artifacts.get(artifacts.size() - 1).getAsJsonObject();
		return dllArtifact.get("relativePath").getAsString();
	}

	@Override
	public Date getUpdatedOn() throws IOException {
		long timestamp = getJson().get("timestamp").getAsLong();
		return new Date(timestamp);
	}
	
	private boolean isBuildSuccess() throws IOException {
		String result = getJson().get("result").getAsString();
		return result != null && result.toLowerCase().equals("success");
	}

	@Override
	public boolean isUpdateAvailable(Date lastUpdated, String lastFileName) {
		try {
			return isBuildSuccess() && super.isUpdateAvailable(lastUpdated, lastFileName);
		} catch (IOException e) {
			return false;
		}
	}

}
