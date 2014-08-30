package aohara.tinkertime.models;

import java.net.URL;

public interface UpdateListener {
	
	public void setUpdateAvailable(URL pageUrl, String newestFileName);

}
