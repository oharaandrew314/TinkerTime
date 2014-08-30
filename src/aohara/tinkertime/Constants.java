package aohara.tinkertime;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class Constants {
	
	public static final String
		HOST_CURSE = "www.curse.com",
		HOST_GITHUB = "github.com",
		HOST_MODULE_MANAGER = "ksp.sarbian.com";
	public static final String[] ACCEPTED_MOD_HOSTS = {HOST_CURSE, HOST_GITHUB};
	
	public static final String MODULE_MANAGER_ARTIFACT_DL_URL = (
		"https://ksp.sarbian.com/jenkins/job/ModuleManager/lastSuccessfulBuild/artifact/"
	);
	
	public static URL getModuleManagerJenkinsUrl(){
		try {
			return new URL("https://ksp.sarbian.com/jenkins/job/ModuleManager/lastSuccessfulBuild/api/json");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static URL getTinkerTimeGithubUrl(){
		try {
			return new URL("https://github.com/oharaandrew314/TinkerTime/releases");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static URL checkModUrl(URL url) throws MalformedURLException {
		String host = url.getHost().toLowerCase();
		String path = url.getPath().toLowerCase();

		if (host.equals(HOST_GITHUB) && !path.endsWith("/releases")){
			return new URL(url.toString() + "/releases");
		} else if (Arrays.asList(ACCEPTED_MOD_HOSTS).contains(host)){
			return url;
		}
		return null;
	}
	
	public static URL checkModUrl(String url) throws MalformedURLException {
		return checkModUrl(new URL(url));
	}

}
