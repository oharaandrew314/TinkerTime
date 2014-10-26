package aohara.tinkertime.crawlers;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

import aohara.tinkertime.crawlers.pageLoaders.PageLoader;

/**
 * Abstract Base Class for Creating Web Crawlers to gather file information.
 * This Crawler is meant to be controlled by a Workflow since these operations
 * are blocking, and may be long-running. 
 * 
 * @author Andrew O'Hara
 *
 * @param <T> Type of Page that is to be returned by getPage
 */
public abstract class Crawler<T> {
	
	private final PageLoader<T> pageLoader;
	private final URL url;
	
	public Crawler(URL url, PageLoader<T> pageLoader){
		this.url = url;
		this.pageLoader = pageLoader;
	}
	
	public T getPage(URL url) throws IOException {
		return pageLoader.getPage(this, url);
	}
	
	public boolean isSuccesful(){
		return true;
	}
	
	public abstract String generateId();
	public abstract URL getDownloadLink() throws IOException;
	public abstract String getNewestFileName() throws IOException;
	public abstract Date getUpdatedOn() throws IOException;
	public abstract URL getImageUrl() throws IOException;
	public abstract String getName() throws IOException;
	public abstract String getCreator() throws IOException;
	public abstract String getSupportedVersion() throws IOException;
	
	public URL getPageUrl(){
		return url;
	}
	
	public URL getApiUrl(){
		return url;
	}
}
