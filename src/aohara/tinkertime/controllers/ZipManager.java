package aohara.tinkertime.controllers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;

public class ZipManager {
	
	public static Set<String> getFiles(Path zipPath) {
		Set<String> set = new HashSet<>();	
		
		try(ZipFile zipFile = new ZipFile(zipPath.toFile())){
			for (ZipEntry entry : getZipEntries(zipFile, false)){
				set.add(entry.getName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return set;
	}
	
	private static Set<ZipEntry> getZipEntries(ZipFile zipFile, boolean includeDirs){
		Set<ZipEntry> set = new HashSet<>();		
		Enumeration<? extends ZipEntry> entries = zipFile.entries();

	    while(entries.hasMoreElements()){
	    	ZipEntry entry = entries.nextElement();
	    	if (!entry.isDirectory() || includeDirs){
	    		set.add(entry);
	    	}
	        
	    }
		return set;
	}
	
	public static void unzipFile(Path zipPath, Path destPath){
		try(ZipFile zipFile = new ZipFile(zipPath.toFile())){
			for (ZipEntry entry : getZipEntries(zipFile, false)){
				FileUtils.copyInputStreamToFile(
					zipFile.getInputStream(entry),
					destPath.resolve(entry.getName()).toFile()
				);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	public static void deleteZipFiles(Path zipPath, Path filePath){
		try(ZipFile zipFile = new ZipFile(zipPath.toFile())){
			for (ZipEntry entry : getZipEntries(zipFile, true)){
				FileUtils.deleteQuietly(filePath.resolve(entry.getName()).toFile());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
