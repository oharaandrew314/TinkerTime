package aohara.tinkertime.crawlers;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Contains Several Constants for Crawling
 * 
 * @author Andrew O'Hara
 */
public class Constants {
	
	// Host Names
	public static final String
		HOST_CURSE = "www.curse.com",
		HOST_GITHUB = "github.com",
		HOST_MODULE_MANAGER = "ksp.sarbian.com",
		HOST_KERBAL_STUFF = "kerbalstuff.com";
	public static final String[] ACCEPTED_MOD_HOSTS	 = new String[]{
		HOST_CURSE, HOST_GITHUB, HOST_KERBAL_STUFF
	};
	
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
}
