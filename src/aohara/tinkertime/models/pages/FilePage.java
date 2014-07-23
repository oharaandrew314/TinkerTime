package aohara.tinkertime.models.pages;

import java.net.URL;
import java.util.Date;

public interface FilePage {
	
	public abstract Date getUpdatedOn();
	public abstract String getNewestFileName();
	public abstract URL getDownloadLink();
	public abstract boolean isUpdateAvailable(Date lastUpdated);
	public abstract URL getPageUrl();
}
