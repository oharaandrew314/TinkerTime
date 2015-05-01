package aohara.tinkertime.crawlers;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.Callable;

import aohara.common.version.Version;

public class UpdateCheckCrawler implements Callable<Boolean> {
	
	private final Crawler<?> crawler;
	private boolean isUpdateAvailable = false, wasRun = false;
	private final Version currentVersion;
	private final Date lastUpdatedOn;
	
	public UpdateCheckCrawler(Crawler<?> crawler, Version currentVersion, Date lastUpdatedOn){
		this.crawler = crawler;
		this.currentVersion = currentVersion;
		this.lastUpdatedOn = lastUpdatedOn;
	}

	@Override
	public Boolean call() throws IOException {
		try{
			isUpdateAvailable = crawler.getVersion().greaterThan(currentVersion);
		} catch (NullPointerException e){
			try {
				isUpdateAvailable = crawler.getUpdatedOn().before(lastUpdatedOn);
			} catch (NullPointerException | IOException e1) {
				return false;
			}
		}
		wasRun = true;
		return isUpdateAvailable;
	}
	
	public boolean isUpdateAvailable() throws IOException {
		if (!wasRun){
			call();
		}
		return isUpdateAvailable;
	}
	
	public Version getVersion(){
		return crawler.getVersion();
	}
	
	public URL getDownloadLink() throws IOException{
		return crawler.getDownloadLink();
	}

}
