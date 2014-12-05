package aohara.tinkertime.crawlers;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import aohara.tinkertime.crawlers.pageLoaders.PageLoader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Crawler for gathering file data from a Jenkins Json Artifact.
 * 
 * @author Andrew O'Hara
 */
public class JenkinsCrawler extends Crawler<JsonObject> {
	
	private JsonObject cachedJson;
	private final URL artifactDownloadUrl;
	private final String name;
	
	public JenkinsCrawler(URL url, PageLoader<JsonObject> pageLoader, String name, URL artifactDownloadUrl) {
		super(url, pageLoader);
		this.name = name;
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
		
		// ignore milliseconds
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTime();
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
		return name;
	}
	
	@Override
	public boolean isSuccesful(){
		try {
			String result = getJson().get("result").getAsString();
			return result != null && result.toLowerCase().equals("success");
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public String getCreator() throws IOException {
		for (JsonElement culprit : getJson().get("culprits").getAsJsonArray()){
			return culprit.getAsJsonObject().get("fullName").getAsString();
		}
		return null;
	}

	@Override
	public String getSupportedVersion() throws IOException {
		return null;
	}

}
