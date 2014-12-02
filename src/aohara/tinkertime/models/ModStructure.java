package aohara.tinkertime.models;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

import aohara.tinkertime.TinkerConfig;
import thirdParty.ZipNode;

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
	
	private final Set<ZipNode> modules;
	
	private ModStructure(Set<ZipNode> modules){
		this.modules = modules;
	}
	
	public boolean usesModule(ZipNode module){
		for (ZipNode m : modules){
			if (m.getName().equals(module.getName())){
				return true;
			}
		}
		return false;
	}
	
	public Set<ZipNode> getModules(){
		return new HashSet<ZipNode>(modules);
	}
	
	// Factory Methods
	
	public static ModStructure inspectArchive(TinkerConfig config, Mod mod) throws IOException {
		return inspectArchive(mod.getCachedZipPath(config));
	}
	
	public static ModStructure inspectArchive(final Path zipPath) throws IOException {
		try(ZipFile zipFile = new ZipFile(zipPath.toFile())){
			ZipNode root = ZipNode.fromZipFile(zipFile); // Get structure of zip
			
			// Find GameData Path within zip
			ZipNode gameData = getGameDataNode(root);
			gameData = gameData != null ? gameData : root;
			
			// Discover structure
			return new ModStructure(getModules(gameData));
		}
	}
	
	public static String getReadmeText(final TinkerConfig config, final Mod mod){
		Path zipPath = mod.getCachedZipPath(config);
		if (zipPath != null){
			try(ZipFile zipFile = new ZipFile(zipPath.toFile())){
				return getReadmeText(zipFile);
			} catch (IOException e) {}
		}
		return null;
	}
	
	private static String getReadmeText(final ZipFile zipFile){
		for (ZipEntry entry : new HashSet<ZipEntry>(Collections.list(zipFile.entries()))){
			if (!entry.isDirectory() && entry.getName().toLowerCase().contains("readme")){
				try(StringWriter writer = new StringWriter()){
					IOUtils.copy(zipFile.getInputStream(entry), writer);
					return writer.toString();
				} catch (IOException e) {}
			}
		}
		return null;
	}
	
	private static ZipNode getGameDataNode(final ZipNode zipNode){
		if (zipNode.getName().toLowerCase().equals("gamedata/")){
			return zipNode;
		} else {
			for (ZipNode child : zipNode.getChildren().values()){
				ZipNode result = getGameDataNode(child);
				if (result != null){
					return result;
				}
			}
		}
		return null;
	}

	private static Set<ZipNode> getModules(final ZipNode gameDataNode){
		Set<ZipNode> modules = new HashSet<>();
		
		for (ZipNode child : gameDataNode.getChildren().values()){
			if (child.isDirectory()){
				modules.add(child);
			}
		}
		
		return modules;
	}
}
