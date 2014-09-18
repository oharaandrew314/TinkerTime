package aohara.tinkertime.models;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

import aohara.tinkertime.Config;

/**
 * Model for discovering and reporting the structure of a Mod Zip File.
 * 
 * Mods can contain a Readme, and contain at least one Module.  Modules can contain
 * the main mod data, or its bundled dependency data.
 * 
 * @author Andrew O'Hara
 *
 */
public class ModStructure {
	
	private String readmeName;
	private Set<Module> modules = new HashSet<>();
	public final Path zipPath;
	
	public ModStructure(Mod mod, Config config){
		this(config.getModZipPath(mod));
	}
	
	public ModStructure(Path zipPath){
		this.zipPath = zipPath;
		
		Path gameDataPath = null;
		Set<ZipEntry> entries = getZipEntries();
		
		// TODO: Perform Discovery in a Factory Class
		
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
	
	private Set<ZipEntry> getZipEntries() {
		Set<ZipEntry> set = new HashSet<>();
		
		try (ZipFile zipFile = new ZipFile(zipPath.toFile())){
			set.addAll(Collections.list(zipFile.entries()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return set;
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
		try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {
			ZipEntry entry = zipFile.getEntry(readmeName);

			StringWriter writer = new StringWriter();
			IOUtils.copy(zipFile.getInputStream(entry), writer);
			return writer.toString();
		} catch (IOException | NullPointerException e) {
			return null;
		}
	}
	
	public Set<Module> getModules(){
		return new HashSet<Module>(modules);
	}
	
	private Path toPath(ZipEntry entry){
		return Paths.get(entry.getName());
	}
	
	// -- Module Class ------------------
	
	/**
	 * Represents a Folder that would potentially be added to the KSP GameData
	 * directory.
	 * 
	 * @author Andrew O'Hara
	 */
	public class Module {

		private final Path pathWithinZip;
		private final Set<ZipEntry> entries = new HashSet<>();

		private Module(Path zipPath, Path pathWithinZip) {
			this.pathWithinZip = pathWithinZip;

			// TODO: Move discovery to Factory
			for (ZipEntry entry : getZipEntries()) {
				if (!entry.isDirectory() && toPath(entry).startsWith(pathWithinZip)){
					entries.add(entry);
				}
			}
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
