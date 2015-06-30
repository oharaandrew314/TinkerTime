package aohara.tinkertime.models;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Calendar;

import aohara.common.version.Version;
import aohara.common.version.VersionParser;
import aohara.tinkertime.io.crawlers.Crawler;

public class ModFactory {
	
	public static Mod newTempMod(Path zipPath){
		String fileName = zipPath.getFileName().toString();
		String prettyName = fileName;
		if (prettyName.indexOf(".") > 0) {
			prettyName = prettyName.substring(0, prettyName.lastIndexOf("."));
		}
		return new Mod(
			prettyName, fileName, null, null,
			Calendar.getInstance().getTime(), null,
			Version.valueOf(VersionParser.parseVersionString(prettyName))
		);
	}
	
	public static Mod newTempMod(Crawler<?> crawler) throws MalformedURLException{
		return newTempMod(crawler.getApiUrl(), null);
	}
	
	public static Mod newTempMod(URL url, Version version){
		return new Mod(
				String.format("New %s Mod",
				url.getHost()), null, null, url, null, null, version
			);
	}
}
