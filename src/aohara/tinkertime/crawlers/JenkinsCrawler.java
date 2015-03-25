package aohara.tinkertime.crawlers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import aohara.tinkertime.crawlers.pageLoaders.PageLoader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Crawler for gathering file data from a Jenkins Json Artifact.
 * 
 * @author Andrew O'Hara
 */
public class JenkinsCrawler extends Crawler<JsonElement> {
	
	private JsonObject cachedJson;
	private final String jobName;
	
	public JenkinsCrawler(URL jenkinsUrl, String jobName, PageLoader<JsonElement> pageLoader) throws MalformedURLException{
		super(jenkinsUrl, pageLoader);
		this.jobName = jobName;
	}
	
	public static JenkinsCrawler getCrawler(URL jobUrl, PageLoader<JsonElement> pageLoader) throws MalformedURLException{
		String jobName = jobUrl.getPath().split("job/")[1];
		if (jobName.contains("/")){
			jobName = jobName.split("/")[0];
		}
		return new JenkinsCrawler(jobUrl, jobName, pageLoader);
	}
	
	@Override
	public URL getApiUrl(){
		try {
			return new URL(getPageUrl(), String.format("job/%s/lastSuccesfulBuild/api/json", jobName)
			);
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
	public String generateId() {
		return jobName;
	}

	@Override
	public URL getImageUrl() throws IOException {
		return null;
	}

	@Override
	public String getName() throws IOException {
		return jobName;
	}

	@Override
	public String getCreator() throws IOException {
		return getPageUrl().getHost();
	}

	@Override
	public String getSupportedVersion() throws IOException {
		return null;
	}

	@Override
	protected Collection<Asset> getNewestAssets() throws IOException {
		Collection<Asset> assets = new LinkedList<>();
		
		JsonArray artifacts = getJson().get("artifacts").getAsJsonArray();
		JsonObject dllArtifact = artifacts.get(artifacts.size() - 1).getAsJsonObject();
		String fileName = dllArtifact.get("relativePath").getAsString();
		
		assets.add(new Asset(
			fileName,
			new URL(getPageUrl(), String.format("job/%s/lastSuccesfulBuild/artifact", fileName))
		));
		
		return assets;
	}

	@Override
	public String getVersionString() throws IOException {
		return getJson().get("number").getAsString();
	}
}
