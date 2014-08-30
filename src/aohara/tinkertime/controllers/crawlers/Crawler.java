package aohara.tinkertime.controllers.crawlers;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

import aohara.tinkertime.controllers.crawlers.pageLoaders.PageLoader;

public abstract class Crawler<R, T> implements PageLoader<T> {
	
	private final PageLoader<T> pageLoader;
	public final URL url;
	
	public Crawler(URL url, PageLoader<T> pageLoader){
		this.url = url;
		this.pageLoader = pageLoader;
	}
	
	@Override
	public T getPage(URL url) throws IOException {
		return pageLoader.getPage(url);
	}
	
	public abstract R crawl() throws IOException;
	public abstract URL getDownloadLink() throws IOException;
	public abstract String getNewestFileName() throws IOException;
	protected abstract Date getUpdatedOn() throws IOException;
	
	public boolean isUpdateAvailable(Date lastUpdated, String lastFileName) {
		try {
			if (lastUpdated != null){
				return getUpdatedOn().compareTo(lastUpdated) > 0;
			} else if (lastFileName != null){
				return getNewestFileName().equals(lastFileName);
			}
			return true;
		} catch (IOException e){
			e.printStackTrace();
			return false;
		}
	}
}
