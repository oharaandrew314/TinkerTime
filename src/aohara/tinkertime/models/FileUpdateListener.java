package aohara.tinkertime.models;

import java.net.URL;

/**
 * Public Interface for notfiying a Class that an updated version of a file is available.
 * 
 * @author Andrew O'Hara
 */
public interface FileUpdateListener {
	
	public void setUpdateAvailable(URL pageUrl, String newestFileName);

}
