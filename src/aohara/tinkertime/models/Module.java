package aohara.tinkertime.models;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Represents a Folder that would potentially be added to the KSP GameData
 * directory.
 * 
 * @author Andrew O'Hara
 */
public class Module {

	private final Path zipPath, modulePath;
	private final Set<ZipEntry> entries;
	
	public Module(Path zipPath, ZipEntry moduleEntry, Collection<ZipEntry> entries){
		this.zipPath = zipPath;
		this.entries = new HashSet<>(entries);
		modulePath = Paths.get(moduleEntry.getName());
	}
	
	public Map<ZipEntry, Path> getContent(){
		Map<ZipEntry, Path> paths = new HashMap<>();
		
		try (ZipFile zipFile = new ZipFile(zipPath.toFile())){
			for (ZipEntry entry : entries){
				Path relPath = modulePath.relativize(Paths.get(entry.getName()));
				paths.put(entry, Paths.get(getName()).resolve(relPath));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return paths;
	}
	
	public String getName(){
		return modulePath.getFileName().toString();
	}
}
