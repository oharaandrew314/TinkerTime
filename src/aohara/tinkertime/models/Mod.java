package aohara.tinkertime.models;

import java.net.URL;

import aohara.tinkertime.controllers.ModManager.CannotAddModException;
import aohara.tinkertime.models.pages.ModPage;

public class Mod extends DownloadedFile {
	
	private String creator, currentFile, name;
	private URL imageUrl;
	private boolean enabled = false;
	private transient boolean updateAvailable = false;
	
	public Mod(ModPage page) throws CannotAddModException {
		super(page);
		updateModData(page);
	}
	
	public String getName(){
		return name;
	}

	public String getCreator() {
		return creator;
	}

	public String getNewestFileName() {
		return currentFile;
	}

	public URL getImageUrl() {
		return imageUrl;
	}
	
	// -- Other Methods --------------------
	
	public boolean isEnabled(){
		return enabled;
	}
	
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}
	
	public void updateModData(ModPage page) throws CannotAddModException{
		super.update(page);
		try{
			name = page.getName();
			creator = page.getCreator();
			currentFile = page.getNewestFileName();
			imageUrl = page.getImageUrl();
			updateAvailable = false;
		} catch(NullPointerException e){
			e.printStackTrace();
			throw new CannotAddModException();
		}
	}
	
	public void setUpdateAvailable(){
		updateAvailable = true;
	}
	
	public boolean isUpdateAvailable(){
		return updateAvailable;
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof Mod){
			return ((Mod)o).getName().equals(getName());
		}
		return false;
	}
	
	@Override
	public String toString(){
		return getName();
	}
}
