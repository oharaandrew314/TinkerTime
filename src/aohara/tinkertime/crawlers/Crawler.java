package aohara.tinkertime.crawlers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import aohara.common.version.Version;
import aohara.common.version.VersionParser;
import aohara.tinkertime.crawlers.pageLoaders.PageLoader;
import aohara.tinkertime.models.Mod;


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
	
	public static final String[] VALID_ASSET_EXTENSIONS = new String[]{ ".zip", ".dll" };
	
	private final PageLoader<T> pageLoader;
	private final Integer existingModId;
	public final URL pageUrl;
	
	private Asset cachedAsset;
	private AssetSelector assetSelector = new DialogAssetSelector();
	
	
	public Crawler(URL url, PageLoader<T> pageLoader, Integer existingModId) {
		this.pageUrl = url;
		this.pageLoader = pageLoader;
		this.existingModId = existingModId;
	}
	
	public T getPage(URL url) throws IOException {
		return pageLoader.getPage(url);
	}
	
	public abstract URL getImageUrl() throws IOException;
	public abstract Date getUpdatedOn() throws IOException;

	protected abstract String getName() throws IOException;
	protected abstract String getCreator() throws IOException;
	protected abstract String getKspVersion() throws IOException;
	protected abstract String getVersionString() throws IOException;
	protected abstract Collection<Asset> getNewestAssets() throws IOException;
	
	public URL getApiUrl() throws MalformedURLException{
		return pageUrl;
	}
	
	public Version getVersion(){
		try {
			// First try to parse version from an available version tag field
			String versionString = VersionParser.parseVersionString(getVersionString());
			return Version.valueOf(versionString);
		} catch(IOException | IllegalArgumentException e){
			try {
				// Alternately, try to parse version from the fileName
				String versionString = VersionParser.parseVersionString(getNewestFileName());
				return Version.valueOf(versionString);
			} catch (IOException | IllegalArgumentException e1) {
				return null;
			}
		}
	}
	
	private Asset getSelectedAsset() throws IOException {
		// If there is no cached selected asset, get it
		if (cachedAsset == null){
			// Get Valid Assets so one can be chosen
			Collection<Asset> assets = new LinkedList<>();
			for (Asset asset : getNewestAssets()){
				for (String validAssetExtension : VALID_ASSET_EXTENSIONS){
					if (asset.fileName.endsWith(validAssetExtension)){
						assets.add(asset);
					}
				}
			}
			
			switch(assets.size()){
			case 0:
				// No non-source downloads
				throw new IOException("No releases found for this mod");
			case 1:
				// One asset; use it by default
				return assets.iterator().next();
			default:
				// Ask user which asset to use
				cachedAsset = assetSelector.selectAsset(getName(), getNewestAssets());
			}
		}
		
		if (cachedAsset == null){
			throw new IOException("You must select a download to use the mod!");
		}
		return cachedAsset;
	}
	
	private final String getNewestFileName() throws IOException {
		return getSelectedAsset().fileName;
	}
	
	public void testConnection() throws IOException {
		getPage(getApiUrl());
	}
	
	// -- Public Methods ---------------------------------------------------

	public void setAssetSelector(AssetSelector assetSelector){
		this.assetSelector = assetSelector;
	}

	public Mod getMod() throws IOException {
		return new Mod(
			existingModId, getName(), getNewestFileName(),
			getCreator(), pageUrl,
			getUpdatedOn() != null ? getUpdatedOn() : Calendar.getInstance().getTime(),
			getKspVersion(), getVersion()
		);
	}
	
	public final URL getDownloadLink() throws IOException{
		return getSelectedAsset().downloadLink;
	}
	
	// -- Inner Asset Class ---------------------------------------------------
	
	public static class Asset {
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
	
	public static interface AssetSelector {
		
		public Asset selectAsset(String modName, Collection<Asset> assets);
	}
	
	static class DialogAssetSelector implements AssetSelector {

		@Override
		public Asset selectAsset(String modName, Collection<Asset> assets) {
			return (Asset) JOptionPane.showInputDialog(null,
				"Which variant of the mod '" + modName + "' should be used?",
				"Multiple Downloads Available",
				JOptionPane.QUESTION_MESSAGE,
				null,
				assets.toArray(),
				assets.iterator().next()
			);
		}
	}
}
