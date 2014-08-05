package aohara.tinkertime;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class Constants {
	
	public static final String
		CURSE_HOST = "www.curse.com",
		GITHUB_HOST = "github.com";
	
	public static String[] ACCEPTED_MOD_HOSTS = {CURSE_HOST, GITHUB_HOST};
	
	public static URL getModuleManagerJenkinsUrl(){
		try {
			return new URL("https://ksp.sarbian.com/jenkins/job/ModuleManager/lastSuccessfulBuild/api/json");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static URL checkModUrl(URL url) throws MalformedURLException {
		String host = url.getHost().toLowerCase();
		String path = url.getPath().toLowerCase();

		if (host.equals(GITHUB_HOST) && !path.endsWith("/releases")){
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
