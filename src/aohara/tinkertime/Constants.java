package aohara.tinkertime;

import java.net.MalformedURLException;
import java.net.URL;

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

}
