package aohara.tinkertime.models;

import java.net.URL;
import java.util.Date;

public class Mod implements ModApi{
	
	private final String name, creator, newestFile;
	private final Date lastUpdated;
	private final URL downloadLink, imageUrl, pageUrl;
	private boolean enabled = false;
	
	public Mod(ModApi page){
		name = page.getName();
		creator = page.getCreator();
		newestFile = page.getNewestFile();
		
		lastUpdated = page.getUpdatedOn();
		
		downloadLink = page.getDownloadLink();
		imageUrl = page.getImageUrl();
		pageUrl = page.getPageUrl();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Date getUpdatedOn() {
		return lastUpdated;
	}

	@Override
	public String getCreator() {
		return creator;
	}

	@Override
	public String getNewestFile() {
		return newestFile;
	}

	@Override
	public URL getDownloadLink() {
		return downloadLink;
	}

	@Override
	public URL getImageUrl() {
		return imageUrl;
	}

	@Override
	public URL getPageUrl() {
		return pageUrl;
	}
	
	// -- Other Methods --------------------
	
	public boolean isNewer(ModApi mod){
		return mod.getUpdatedOn().compareTo(getUpdatedOn()) > 0;
	}
	
	public boolean isEnabled(){
		return enabled;
	}
	
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}
	
}
