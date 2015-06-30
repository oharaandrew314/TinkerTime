package aohara.tinkertime.models;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

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
	
	private final Map<Path, ZipEntry> zipEntries = new LinkedHashMap<>();
	private final Path zipPath;
	private String readmeText;
	private boolean loaded = false;
	
	public ModStructure(Path zipPath){
		this.zipPath = zipPath;
	}
	
	// Factory Methods
	
	private boolean isZip(){
		return zipPath.toString().endsWith(".zip");
	}
	
	private void inspectArchive() throws IOException {
		if (zipPath == null){
			throw new FileNotFoundException();
		} else if(!zipPath.toFile().exists()){
			throw new FileNotFoundException(zipPath.toString());
		}
		
		if (!isZip()){
			zipEntries.put(zipPath.getFileName(), null);
		} else {
			try(ZipFile zipFile = new ZipFile(zipPath.toFile())){
				Path gameDataPath = null;
				
				//Make first pass of entries to get key information 
				Enumeration<? extends ZipEntry> entries = zipFile.entries();
				for (ZipEntry entry; entries.hasMoreElements(); ){
					entry = entries.nextElement();
					
					// Find Gamedata path
					if (gameDataPath == null && entry.getName().toLowerCase().contains("gamedata")){
						
						// Once a candidate has been found, find the exact path to the folder
						Path tempPath = Paths.get(entry.getName());
						while(tempPath != null && gameDataPath == null){
							if (tempPath.getFileName().toString().toLowerCase().equals("gamedata")){
								gameDataPath = tempPath;
							} else {
								tempPath = tempPath.getParent();
							}
						}
					}
					
					// Find Readme text
					if (readmeText == null && !entry.isDirectory() && entry.getName().toLowerCase().contains("readme")){
						try(StringWriter writer = new StringWriter(); InputStream is = zipFile.getInputStream(entry)){
							IOUtils.copy(is, writer);
							readmeText = writer.toString();
						} catch (IOException e) {}
					}
				}
				
				// Make second pass of entries to get all Mod Files
				entries = zipFile.entries();
				if (gameDataPath == null){
					// If no gameDataPath, get all files with a path length of at least 2.
					// This is because, we only get files which are within folders in the root of the zip
					for (ZipEntry entry; entries.hasMoreElements(); ){
						entry = entries.nextElement();
						Path entryPath = Paths.get(entry.getName());
						if (entryPath.getNameCount() >= 2){
							zipEntries.put(entryPath, entry);
						}
					}
				} else {
					// Get all files within the GameData directory
					for (ZipEntry entry; entries.hasMoreElements(); ){
						entry = entries.nextElement();
						Path entryPath = Paths.get(entry.getName());
						if (
							entryPath.startsWith(gameDataPath) && !entryPath.equals(gameDataPath) &&
							!(entry.getName().contains("ModuleManager") && entry.getName().endsWith(".dll"))
						){
							zipEntries.put(gameDataPath.relativize(entryPath), entry);
						}
					}
				}
			}
		}
		
		// Ensure that all folders added to zipEntries
		for (Path path : new LinkedHashSet<Path>(zipEntries.keySet())){
			while(path.getParent() != null){
				path = path.getParent();
				zipEntries.put(path, null);
			}
		}
	}
	
	public Set<Path> getPaths() throws IOException{
		ensureLoaded();
		return new LinkedHashSet<>(zipEntries.keySet());
	}
	
	public Map<Path, ZipEntry> getZipEntries() throws IOException{
		ensureLoaded();
		return new LinkedHashMap<>(zipEntries);
	}
	
	public String getReadmeText() throws IOException{
		ensureLoaded();
		return readmeText;
	}
	
	private void ensureLoaded() throws IOException{
		if (!loaded){
			inspectArchive();
			loaded = true;
		}
	}
}
