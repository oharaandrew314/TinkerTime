package aohara.tinkertime.models;

import aohara.tinkertime.crawlers.Crawler;

/**
 * Public Interface for notfiying a Class that an updated version of a file is available.
 * 
 * @author Andrew O'Hara
 */
public interface FileUpdateListener {
	
	public void setUpdateAvailable(Crawler<?> crawler);

}
