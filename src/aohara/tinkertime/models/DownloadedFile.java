package aohara.tinkertime.models;

import java.net.URL;
import java.util.Date;

import aohara.tinkertime.models.pages.FilePage;

public class DownloadedFile implements UpdateListener {
	
	private String fileName;
	private Date lastUpdatedOn;
	private URL downloadLink, pageUrl;
	
	public DownloadedFile(FilePage page){
		update(page);
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

	public boolean isUpdateAvailable(FilePage page) {
		return getUpdatedOn().compareTo(page.getUpdatedOn()) > 0;
	}
	
	public void update(FilePage page){
		fileName = page.getNewestFileName();
		lastUpdatedOn = page.getUpdatedOn();
		downloadLink = page.getDownloadLink();
		pageUrl = page.getPageUrl();
	}
	
	@Override
	public boolean equals(Object o){
		return (
			o instanceof DownloadedFile
			&& ((DownloadedFile)o).getPageUrl().equals(getPageUrl())
		);
	}

	@Override
	public void setUpdateAvailable(FilePage latest) {
		// No action
	}
}
