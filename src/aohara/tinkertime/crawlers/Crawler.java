package aohara.tinkertime.crawlers;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;

import javax.swing.JOptionPane;

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
	
	private Asset cachedAsset;
	private final PageLoader<T> pageLoader;
	private final URL url;
	
	public Crawler(URL url, PageLoader<T> pageLoader){
		this.url = url;
		this.pageLoader = pageLoader;
	}
	
	public T getPage(URL url) throws IOException {
		return pageLoader.getPage(this, url);
	}

	public abstract String generateId();
	public abstract Date getUpdatedOn() throws IOException;
	public abstract URL getImageUrl() throws IOException;
	public abstract String getName() throws IOException;
	public abstract String getCreator() throws IOException;
	public abstract String getSupportedVersion() throws IOException;
	protected abstract Collection<Asset> getNewestAssets() throws IOException;
	
	public boolean isAssetsAvailable(){
		try {
			return !getNewestAssets().isEmpty();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public URL getPageUrl(){
		return url;
	}
	
	public URL getApiUrl(){
		return url;
	}
	
	public boolean isUpdateAvailable(Date lastUpdated, String lastFileName) {
		try {
			if (lastUpdated != null && getUpdatedOn() != null){  // Prefer to compare update dates
				return getUpdatedOn().compareTo(lastUpdated) > 0;
			} else if (lastFileName != null){ // Alternately, compare file names
				for (Asset asset : getNewestAssets()){
					if (asset.fileName.equals(lastFileName)){
						return false;
					}
				}
			}
			return true;  // Finally, just assume an update is available
		} catch (IOException e){
			e.printStackTrace();
			return false;
		}
	}
	
	private Asset getSelectedAsset() throws IOException {
		// If there is no cached selected asset, get it
		if (cachedAsset == null){
			Collection<Asset> assets = getNewestAssets();
			switch(assets.size()){
			case 0:
				// No non-source downloads
				throw new IOException("No releases found for this mod");
			case 1:
				// One asset; use it by default
				return assets.iterator().next();
			default:
				// Ask user which asset to use
				cachedAsset = (Asset) JOptionPane.showInputDialog(null,
						"Which variant of the mod '" + getName() + "' should be used?",
						"Multiple Downloads Available",
						JOptionPane.QUESTION_MESSAGE,
						null,
						assets.toArray(),
						assets.iterator().next()
				);
				
				if (cachedAsset == null){
					throw new IOException("You must select a download to use the mod!");
				}
			}
		}
		
		return cachedAsset;
	}
	
	public final String getNewestFileName() throws IOException {
		return getSelectedAsset().fileName;
	}
	
	public final URL getDownloadLink() throws IOException{
		return getSelectedAsset().downloadLink;
	}
	
	// -- Inner Asset Class ---------------------------------------------------
	
	static class Asset {
		public final String fileName;
		public final URL downloadLink;
		
		protected Asset(String fileName, URL downloadLink){
			this.fileName = fileName;
			this.downloadLink = downloadLink;
		}
		
		@Override
		public String toString(){
			return fileName;
		}
	}
}
