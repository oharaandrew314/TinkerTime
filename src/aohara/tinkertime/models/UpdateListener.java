package aohara.tinkertime.models;

import aohara.tinkertime.models.pages.FilePage;

public interface UpdateListener {
	
	public void setUpdateAvailable(FilePage latest);

}
