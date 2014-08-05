package aohara.tinkertime.models.pages;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ModuleManagerPage extends Page implements FilePage {
	
	public static final String
		LAST_ARTIFACT_URL = (
			"https://ksp.sarbian.com/jenkins/job/ModuleManager" +
			"/lastSuccessfulBuild/api/json"),
		ARTIFACT_DOWNLOAD_URL = (
			"https://ksp.sarbian.com/jenkins/job/ModuleManager/"
			+ "lastSuccessfulBuild/artifact/"
		);
	
	private final JsonObject obj; 
	
	public static ModuleManagerPage loadPage(JsonObject obj) throws MalformedURLException{
		URL pageUrl = new URL(LAST_ARTIFACT_URL);
		return new ModuleManagerPage(pageUrl, obj);
	}
	
	public ModuleManagerPage(URL pageUrl, JsonObject obj){
		super(pageUrl);
		this.obj = obj;
	}

	@Override
	public URL getDownloadLink(){
		try {
			return new URL(
				new URL(ARTIFACT_DOWNLOAD_URL),
				getNewestFileName()
			);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	@Override
	public Date getUpdatedOn() {
		long timestamp = obj.get("timestamp").getAsLong();
		return new Date(timestamp);
	}

	@Override
	public String getNewestFileName() {
		JsonArray artifacts = obj.get("artifacts").getAsJsonArray();
		JsonObject dllArtifact = artifacts.get(artifacts.size() - 1).getAsJsonObject();
		return dllArtifact.get("relativePath").getAsString();
	}
	
	private boolean isBuildSuccess(){
		String result = obj.get("result").getAsString();
		return result != null && result.toLowerCase().equals("success");
	}

	@Override
	public boolean isUpdateAvailable(Date lastUpdated) {
		return isBuildSuccess() && getUpdatedOn().compareTo(lastUpdated) > 0;
	}
}
