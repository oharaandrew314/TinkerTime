package aohara.tinkertime.content;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

import aohara.tinkertime.Config;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModStructure;
import aohara.tinkertime.models.Module;

public class ArchiveInspector {
	
	public static ModStructure inspectArchive(Config config, Mod mod) {
		return inspectArchive(mod.getCachedZipPath(config));
	}
	
	public static ModStructure inspectArchive(Path zipPath){
		Set<Module> modules = new HashSet<>();
		String readmeText = null;
		
		try(ZipFile zipFile = new ZipFile(zipPath.toFile())){
			
			Set<ZipEntry> entries = new HashSet<>(Collections.list(zipFile.entries()));
			
			for (Path modulePath : getModulePaths(entries, getGameDataPath(entries))){
				modules.add(discoverModule(zipPath, entries, modulePath));
			}
			
			readmeText = getReadmeText(zipFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new ModStructure(zipPath, modules, readmeText);
	}
	
	public static String getReadmeText(Config config, Mod mod){
		try(ZipFile zipFile = new ZipFile(mod.getCachedZipPath(config).toFile())){
			return getReadmeText(zipFile);
		} catch (IOException e) {}
		return null;
	}
	
	private static String getReadmeText(ZipFile zipFile){		
		for (ZipEntry entry : new HashSet<>(Collections.list(zipFile.entries()))){
			if (!entry.isDirectory() && entry.getName().toLowerCase().contains("readme")){
				try(StringWriter writer = new StringWriter()){
					IOUtils.copy(zipFile.getInputStream(entry), writer);
					return writer.toString();
				} catch (IOException e) {}
			}
		}
		return null;
	}
	
	private static Path getGameDataPath(Collection<ZipEntry> entries){
		int shortestLength = Integer.MAX_VALUE;
		ZipEntry gameDataEntry = null;
		
		for (ZipEntry entry : entries){
			if (entry.isDirectory()){
				if (entry.getName().toLowerCase().endsWith("gamedata/")){
					int pathLength = Paths.get(entry.getName()).getNameCount();
					if (pathLength < shortestLength){
						shortestLength = pathLength;
						gameDataEntry = entry;
					}
				}
			}
		}
		return gameDataEntry != null ? Paths.get(gameDataEntry.getName()) : null;
	}
	
	private static Set<Path> getModulePaths(Collection<ZipEntry> entries, Path gameDataPath){
		Set<Path> moduleEntries = new HashSet<>();
		for (ZipEntry entry : entries){
			Path entryPath = Paths.get(entry.getName());
			if (gameDataPath == null){
				if (entryPath.getNameCount() == 2){
					moduleEntries.add(entryPath.getParent());
				}
			} else if (entry.isDirectory() && entryPath.startsWith(gameDataPath) && !entryPath.equals(gameDataPath)){
				Path rel = gameDataPath.relativize(entryPath);
				if (rel.getNameCount() == 1){
					moduleEntries.add(entryPath);
				}
			}
		}
		return moduleEntries;
	}
	
	private static Module discoverModule(Path zipPath, Collection<ZipEntry> entries, Path modulePath){		
		Set<ZipEntry> moduleEntries = new HashSet<>();
		for (ZipEntry entry : entries){
			Path entryPath = Paths.get(entry.getName());
			if (!entry.isDirectory() && entryPath.startsWith(modulePath)){
				moduleEntries.add(entry);
			}
		}
		
		return new Module(zipPath, modulePath, moduleEntries);
	}

}
