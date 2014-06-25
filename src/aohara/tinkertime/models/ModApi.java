package aohara.tinkertime.models;

import java.net.URL;
import java.util.Date;

public interface ModApi {

	public abstract String getName();

	public abstract Date getUpdatedOn();

	public abstract String getCreator();

	public abstract String getNewestFile();

	public abstract URL getDownloadLink();

	public abstract URL getImageUrl();

	public abstract URL getPageUrl();

}