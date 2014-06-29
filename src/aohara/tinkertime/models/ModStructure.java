package aohara.tinkertime.models;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;

import aohara.tinkertime.config.Config;
import aohara.tinkertime.controllers.files.ZipManager;

public class ModStructure {
	
	public final Path zipPath;
	private String readmeName;
	private Set<Module> modules = new HashSet<>();
	
	public ModStructure(Mod mod){
		this(new Config().getModZipPath(mod));
	}
	
	public ModStructure(Path zipPath){
		this.zipPath = zipPath;
		
		Path gameDataPath = null;
		Set<ZipEntry> entries = ZipManager.getZipEntries(zipPath);
		
		// Search for Key Zip Entries
		for (ZipEntry entry : entries){
			// Find Readme
			if (!entry.isDirectory() && entry.getName().toLowerCase().contains("readme")){
				readmeName = entry.getName();
			}
			
			// Find GameData Directory
			if (entry.isDirectory()){
				if (entry.getName().toLowerCase().endsWith("gamedata/")){
					gameDataPath = Paths.get(entry.getName());
				}	
			}
		}
		
		// Search for Modules
		for (ZipEntry entry : entries){
			if (entry.isDirectory()){
				Path entryPath = toPath(entry);
				if (!entryPath.equals(gameDataPath)){
					Path rel = gameDataPath != null ? gameDataPath.relativize(entryPath) : entryPath;
					if (rel.getNameCount() == 1){
						modules.add(new Module(entryPath));
					}
				}
			}
		}
	}
	
	public boolean usesModule(Module module){
		for (Module m : modules){
			if (m.getName().equals(module.getName())){
				return true;
			}
		}
		return false;
	}
	
	public String getReadmeText(){
		return ZipManager.getFileText(zipPath, readmeName);
	}
	
	public Set<Module> getModules(){
		return new HashSet<Module>(modules);
	}
	
	public Path toPath(ZipEntry entry){
		return Paths.get(entry.getName());
	}
	
	// -- Module Class ------------------
	
	public class Module {

		private final Path pathWithinZip;
		private final Set<ZipEntry> entries = new HashSet<>();

		public Module(Path path) {
			this.pathWithinZip = path;

			for (ZipEntry entry : ZipManager.getZipEntries(zipPath)) {
				if (!entry.isDirectory() && toPath(entry).startsWith(path)){
					entries.add(entry);		
				}
			}
		}
		
		public Set<ZipEntry> getEntries(){
			return new HashSet<ZipEntry>(entries);		
		}
		
		public Set<Path> getFilePaths(){
			Set<Path> paths = new HashSet<>();
			for (ZipEntry entry : entries){
				Path relPath = pathWithinZip.relativize(toPath(entry));
				paths.add(Paths.get(getName()).resolve(relPath));
			}
			return paths;
		}
		
		public Path getZipPath(){
			return zipPath;
		}
		
		public String getName(){
			return pathWithinZip.getFileName().toString();
		}

		public boolean isEnabled(){
			for (File file : new Config().getGameDataPath().toFile().listFiles()){
				if (file.isDirectory() && file.getName().equals(getName())){
					return true;
				}
			}
			return false;
		}
	}
}
