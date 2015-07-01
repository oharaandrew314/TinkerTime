package io.andrewohara.tinkertime.io.crawlers;

import io.andrewohara.common.version.Version;
import io.andrewohara.common.version.VersionParser;
import io.andrewohara.tinkertime.io.crawlers.pageLoaders.PageLoader;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;


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

	public static final String ZIP_EXTENSION = ".zip";

	private final PageLoader<T> pageLoader;
	private final Mod mod;

	private Asset cachedAsset;
	private AssetSelector assetSelector = new DialogAssetSelector();

	public Crawler(Mod mod, PageLoader<T> pageLoader) {
		this.mod = mod;
		this.pageLoader = pageLoader;
	}

	/////////
	// Api //
	/////////

	public T getPage(URL url) throws IOException {
		return pageLoader.getPage(url);
	}

	public URL getPageUrl(){
		return mod.getUrl();
	}

	public URL getApiUrl() throws MalformedURLException{
		return getPageUrl();
	}

	public void setAssetSelector(AssetSelector assetSelector){
		this.assetSelector = assetSelector;
	}

	public Mod getMod(){
		return mod;
	}

	public void updatedMod() throws IOException {
		Date updatedOn = getUpdatedOn() != null ? getUpdatedOn() : Calendar.getInstance().getTime();
		mod.update(getName(), getCreator(), updatedOn, getVersion(), getKspVersion());
		mod.setUpdateAvailable(isUpdateAvailable());
	}

	public final URL getDownloadLink() throws IOException{
		return getSelectedAsset().downloadLink;
	}

	public void testConnection() throws IOException{
		getPage(getApiUrl());
	}

	//////////////////////
	// Abstract Methods //
	//////////////////////

	public abstract URL getImageUrl() throws IOException;
	public abstract Date getUpdatedOn() throws IOException;

	protected abstract String getName() throws IOException;
	protected abstract String getCreator() throws IOException;
	protected abstract String getKspVersion() throws IOException;
	protected abstract String getVersionString() throws IOException;
	protected abstract Collection<Asset> getNewestAssets() throws IOException;

	/////////////
	// Helpers //
	/////////////

	private Version getVersion(){
		try {
			// First try to parse version from an available version tag field
			String versionString = VersionParser.parseVersionString(getVersionString());
			return Version.valueOf(versionString);
		} catch(IOException | IllegalArgumentException e){
			try {
				// Alternately, try to parse version from the fileName
				String versionString = VersionParser.parseVersionString(getSelectedAsset().fileName);
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
			List<Asset> assets = new LinkedList<>();
			for (Asset asset : getNewestAssets()){
				if (asset.fileName.endsWith(ZIP_EXTENSION)){
					assets.add(asset);
				}
			}

			switch(assets.size()){
			case 0:
				// No non-source downloads
				throw new IOException("No releases found for this mod");
			case 1:
				// One asset; use it by default
				cachedAsset = assets.get(0);
				break;
			default:
				// Ask user which asset to use
				cachedAsset = assetSelector.selectAsset(getName(), getNewestAssets());
				break;
			}

			if (cachedAsset == null){
				throw new IOException("You must select a download to use the mod!");
			}
		}

		return cachedAsset;
	}

	private boolean isUpdateAvailable(){
		try{
			return getVersion().greaterThan(mod.getModVersion());
		} catch (NullPointerException e){
			try {
				return getUpdatedOn().before(mod.getUpdatedOn());
			} catch (NullPointerException | IOException e1) {
				return false;
			}
		}
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
