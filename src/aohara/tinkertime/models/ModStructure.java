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
	
	private String readmeName;
	private Set<Module> modules = new HashSet<>();
	private final ZipManager zipManager;
	
	public ModStructure(Mod mod, Config config){
		this(config.getModZipPath(mod));
	}
	
	public ModStructure(Path zipPath){
		zipManager = new ZipManager(zipPath);
		
		Path gameDataPath = null;
		Set<ZipEntry> entries = getZipManager().getZipEntries();
		
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
		return getZipManager().getFileText(readmeName);
	}
	
	public Set<Module> getModules(){
		return new HashSet<Module>(modules);
	}
	
	public Path toPath(ZipEntry entry){
		return Paths.get(entry.getName());
	}
	
	public ZipManager getZipManager(){
		return zipManager;
	}
	
	// -- Module Class ------------------
	
	public class Module {

		private final Path pathWithinZip;
		private final Set<ZipEntry> entries = new HashSet<>();

		public Module(Path path) {
			this.pathWithinZip = path;

			for (ZipEntry entry : getZipManager().getZipEntries()) {
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
		
		public String getName(){
			return pathWithinZip.getFileName().toString();
		}

		public boolean isEnabled(Config config){
			for (File file : config.getGameDataPath().toFile().listFiles()){
				if (file.isDirectory() && file.getName().equals(getName())){
					return true;
				}
			}
			return false;
		}
	}
}
