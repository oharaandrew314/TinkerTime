package aohara.tinkertime.crawlers;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import aohara.tinkertime.crawlers.Crawler.Asset;

import com.github.zafarkhaja.semver.Version;

public class VersionInfo implements Comparable<VersionInfo> {
	
	public final String selectedFileName;
	public final Set<String> assetFileNames = new LinkedHashSet<>();
	public final Version version;
	public final Date updatedOn;
	
	public VersionInfo(Version version, Date updatedOn, String fileName){
		this.version = version;
		this.updatedOn = updatedOn;
		selectedFileName = fileName;
	}
	
	public VersionInfo(Version version, Date updatedOn, Collection<Asset> assets){
		this(version, updatedOn, (String) null);
		for (Asset asset : assets){
			assetFileNames.add(asset.fileName);
		}
	}
	
	public boolean hasUpdateDate(){
		return updatedOn != null;
	}
	
	public boolean hasVersion(){
		return version != null;
	}
	
	public boolean hasSelectedFileName(){
		return selectedFileName != null;
	}

	@Override
	public int compareTo(VersionInfo other) {
		if (hasVersion() && other.hasVersion()){ // Prefer to compare versions
			return version.compareTo(other.version);
		} else if (hasUpdateDate() && other.hasUpdateDate()){  // Next, try to compare update dates
			return updatedOn.compareTo(other.updatedOn);
		}
		
		// Finally, try to make an educated guess based on the available file names
		else if (other.assetFileNames.contains(selectedFileName) || assetFileNames.contains(other.selectedFileName)) {
			return 0;
		} else if (other.assetFileNames.equals(assetFileNames)){
			return 0;
		}
		return 1;
	}
}
