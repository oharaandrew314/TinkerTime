package test.util;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import aohara.tinkertime.Config;
import aohara.tinkertime.models.Mod;

public class MockMod extends Mod {
	
	private boolean downloaded = false;

	public MockMod(String modName, String newestFileName, String creator,
			URL imageUrl, URL pageUrl, Date updatedOn, String supportedVersion) {
		super(modName, newestFileName, creator, imageUrl, pageUrl, updatedOn, supportedVersion);
	}
	
	public void setDownloaded(boolean downloaded){
		this.downloaded = downloaded;
	}
	
	@Override
	public Path getCachedZipPath(Config config){
		return downloaded ? ModLoader.getZipPath(getName()) : Paths.get("/");
	}

}
