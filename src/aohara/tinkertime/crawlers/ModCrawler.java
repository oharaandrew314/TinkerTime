package aohara.tinkertime.crawlers;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import aohara.tinkertime.crawlers.pageLoaders.PageLoader;
import aohara.tinkertime.models.Mod;

/**
 * Crawler designed for crawling mods.
 * 
 * Can create a Mod model from the page.
 * 
 * @author Andrew O'Hara
 *
 * @param <T> Type of PageLoader used to load the page
 */
public abstract class ModCrawler<T> extends Crawler<T> {

	public ModCrawler(URL url, PageLoader<T> pageLoader) {
		super(url, pageLoader);
	}
	
	public Mod createMod() throws IOException{
		Date updatedOn = getUpdatedOn() != null ? getUpdatedOn() : Calendar.getInstance().getTime();
		return new Mod(
			generateId(), getName(), getNewestFileName(), getCreator(),
			getImageUrl(), getPageUrl(), updatedOn, getSupportedVersion()
		);
	}
	
	public abstract String getName() throws IOException;
	public abstract URL getImageUrl() throws IOException;
	protected abstract String getCreator() throws IOException;
	
	/**
	 * Returns the version of KSP that this mod version supports.
	 * @return supported KSP Version
	 * @throws IOException if there is an error reading the mod page
	 */
	public abstract String getSupportedVersion() throws IOException;
}
