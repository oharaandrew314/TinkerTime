package aohara.tinkertime.models;

import java.net.URL;
import java.util.Date;

public class Mod extends ModApi{
	
	private String name, creator, newestFile;
	private Date lastUpdated;
	private URL downloadLink, imageUrl, pageUrl;
	private boolean enabled = false;
	
	public Mod(ModApi page){
		updateModData(page);
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
	
	public boolean isEnabled(){
		return enabled;
	}
	
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}
	
	public void updateModData(ModApi mod){
		name = mod.getName();
		creator = mod.getCreator();
		newestFile = mod.getNewestFile();
		
		lastUpdated = mod.getUpdatedOn();
		
		downloadLink = mod.getDownloadLink();
		imageUrl = mod.getImageUrl();
		pageUrl = mod.getPageUrl();
	}
	
	@Override
	public int hashCode(){
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof Mod){
			return ((Mod)o).getName().equals(getName());
		}
		return false;
	}
	
}
