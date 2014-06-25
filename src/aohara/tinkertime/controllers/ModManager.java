package aohara.tinkertime.controllers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;

import aohara.tinkertime.config.Config;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModApi;

public class ModManager {
	
	// -- Path Methods ----------------------
	
	public Path kerbalPath(){
		return new Config().getKerbalPath();
	}
	
	public Path modPath(ModApi mod){
		return new Config().getModPath().resolve(mod.getNewestFile());
	}
	
	// -- State Methods ------------------------
	
	public boolean isEnabled(Mod mod){
		return false;
		//return kerbalPath(mod).toFile().exists();
	}
	
	public boolean isDownloaded(Mod mod){
		return modPath(mod).toFile().exists();
	}
	
	// -- Modifiers ---------------------------------
	
	public void enableMod(Mod mod) throws ModException, IOException {
		if (isEnabled(mod)){
			throw new ModAlreadyEnabledException();
		} else if (!isDownloaded(mod)){
			throw new ModNotDownloadedException();
		}
		
		System.out.println("Enabling " + mod.getName());
		ZipManager.unzipFile(modPath(mod), kerbalPath());
		System.out.println("Enabled " + mod.getName());
	}
	
	public void disableMod(Mod mod) throws ModException {
		if (!isEnabled(mod)){
			throw new ModAlreadyDisabledException();
		}
		
		System.out.println("Disabling " + mod.getName());
		throw new UnsupportedOperationException();
	}
	
	public void downloadMod(ModApi mod) throws IOException, ModException {
		if (modPath(mod).toFile().getName().equals(mod.getName())){
			throw new ModAlreadyDownlodedException();
		}
		
		System.out.println("Downloading " + mod.getNewestFile());
		FileUtils.copyURLToFile(
			mod.getDownloadLink(),
			modPath(mod).toFile()
		);
		System.out.println("Finished downloading " + mod.getNewestFile());
	}
	
	// -- Other ---------------
	
	public Set<String> getFiles(Mod mod) throws ModException {
		if (!isDownloaded(mod)){
			throw new ModNotDownloadedException();
		}
		
		Set<String> set = new HashSet<>();		
		try (ZipFile zipFile = new ZipFile(modPath(mod).toFile())){
			Enumeration<? extends ZipEntry> entries = zipFile.entries();

		    while(entries.hasMoreElements()){
		        set.add(entries.nextElement().getName());
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return set;
	}
	
	// -- Exceptions -----------------------
	
	@SuppressWarnings("serial")
	public abstract class ModException extends Throwable {}
	@SuppressWarnings("serial")
	private class ModAlreadyEnabledException extends ModException {}
	@SuppressWarnings("serial")
	private class ModAlreadyDisabledException extends ModException {}
	@SuppressWarnings("serial")
	private class ModNotDownloadedException extends ModException {}
	@SuppressWarnings("serial")
	private class ModAlreadyDownlodedException extends ModException {}

}
