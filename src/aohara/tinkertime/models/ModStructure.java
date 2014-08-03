package aohara.tinkertime.models;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import aohara.tinkertime.Config;
import aohara.tinkertime.controllers.ZipManager;

public class ModStructure {
	
	private String readmeName;
	private Set<Module> modules = new HashSet<>();
	private final ZipManager zipManager;
	public final Path zipPath;
	
	public ModStructure(Mod mod, Config config){
		this(config.getModZipPath(mod));
	}
	
	public ModStructure(Path zipPath){
		this.zipPath = zipPath;
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
		Set<Path> modulePaths = new HashSet<>();
		for (ZipEntry entry : entries){
			if (!entry.getName().equals(readmeName)){
				Path entryPath = toPath(entry);
				if (gameDataPath == null){
					modulePaths.add(entryPath.subpath(0, 1));
				} else if (entryPath.startsWith(gameDataPath) && !entryPath.equals(gameDataPath)){
					Path rel = gameDataPath.relativize(entryPath);
					if (rel.getNameCount() == 1){
						modulePaths.add(entryPath);
					}					
				}
			}
		}
		
		for (Path modulePath : modulePaths){
			modules.add(new Module(zipPath, modulePath));
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
	
	private Path toPath(ZipEntry entry){
		return Paths.get(entry.getName());
	}
	
	public ZipManager getZipManager(){
		return zipManager;
	}
	
	// -- Module Class ------------------
	
	public class Module {

		private final Path pathWithinZip;
		private final Set<ZipEntry> entries = new HashSet<>();

		private Module(Path zipPath, Path pathWithinZip) {
			this.pathWithinZip = pathWithinZip;

			for (ZipEntry entry : getZipManager().getZipEntries()) {
				if (!entry.isDirectory() && toPath(entry).startsWith(pathWithinZip)){
					entries.add(entry);
				}
			}
		}
		
		public Set<ZipEntry> getEntries(){
			return new HashSet<ZipEntry>(entries);		
		}
		
		public Map<ZipEntry, Path> getOutput(){
			Map<ZipEntry, Path> paths = new HashMap<>();
			
			try (ZipFile zipFile = new ZipFile(zipPath.toFile())){
				for (ZipEntry entry : entries){
					Path relPath = pathWithinZip.relativize(toPath(entry));
					paths.put(entry, Paths.get(getName()).resolve(relPath));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return paths;
		}
		
		public String getName(){
			return pathWithinZip.getFileName().toString();
		}
	}
}
