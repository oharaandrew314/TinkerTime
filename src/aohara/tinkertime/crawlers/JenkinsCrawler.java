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
			cachedJson = getPage(getApiUrl());
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

	@Override
	public String generateId() {
		return getApiUrl().getHost();
	}

	@Override
	public URL getImageUrl() throws IOException {
		return null;
	}

	@Override
	public String getName() throws IOException {
		return "Module Manager";
	}

	@Override
	public String getCreator() throws IOException {
		return "sarbian";
	}

	@Override
	public String getSupportedVersion() throws IOException {
		return null;
	}

}
