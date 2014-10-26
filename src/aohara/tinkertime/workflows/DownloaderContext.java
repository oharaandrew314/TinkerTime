package aohara.tinkertime.workflows;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;

import aohara.tinkertime.crawlers.Crawler;

public abstract class DownloaderContext {
	
	public final Crawler<?> crawler;
	
	protected DownloaderContext(Crawler<?> crawler){
		this.crawler = crawler;
	}
	
	public abstract Path getCachedImagePath() throws IOException;
	public abstract Path getDownloadPath() throws IOException;
	
	public boolean isUpdateAvailable(Date lastUpdated, String lastFileName) {
		try {
			if (!crawler.isSuccesful()){
				return false;
			}else if (lastUpdated != null && crawler.getUpdatedOn() != null){
				return crawler.getUpdatedOn().compareTo(lastUpdated) > 0;
			} else if (lastFileName != null){
				return !crawler.getNewestFileName().equals(lastFileName);
			}
			return true;
		} catch (IOException e){
			e.printStackTrace();
			return false;
		}
	}

}
