package aohara.tinkertime.models;

import java.net.URL;
import java.util.Date;

/**
 * Model for holding information on an updatable File.
 * 
 * @author Andrew O'Hara
 */
public class UpdateableFile implements FileUpdateListener {
	
	private String fileName;
	private Date lastUpdatedOn;
	private URL downloadLink, pageUrl;
	
	public UpdateableFile(String newestFileName, Date lastUpdated, URL pageUrl){
		update(newestFileName, lastUpdated, pageUrl);
	}

	public String getNewestFileName() {
		return fileName;
	}

	public Date getUpdatedOn() {
		return lastUpdatedOn;
	}

	public URL getDownloadLink() {
		return downloadLink;
	}

	public URL getPageUrl() {
		return pageUrl;
	}
	
	public void update(String newestFileName, Date lastUpdated, URL pageUrl){
		this.fileName = newestFileName;
		this.lastUpdatedOn =lastUpdated;
		this.pageUrl = pageUrl;
	}
	
	@Override
	public boolean equals(Object o){
		return (
			o instanceof UpdateableFile
			&& ((UpdateableFile)o).getPageUrl().equals(getPageUrl())
		);
	}
	
	@Override
	public int hashCode(){
		return getPageUrl().hashCode();
	}

	@Override
	public void setUpdateAvailable(URL pageUrl, String newestFileName) {
		// No action
	}
}
