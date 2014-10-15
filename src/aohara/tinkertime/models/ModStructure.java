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

import aohara.tinkertime.Config;
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
	
	public final String readmeText;
	public final ZipNode gameDataNode;
	private final Set<ZipNode> modules;
	public final Path zipPath;
	
	public ModStructure(Path zipPath, ZipNode gameDataNode, Set<ZipNode> modules, String readmeText){
		this.zipPath = zipPath;
		this.gameDataNode = gameDataNode;
		this.modules = modules;
		this.readmeText = readmeText;
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
	
	public static ModStructure inspectArchive(Config config, Mod mod) throws IOException {
		return inspectArchive(mod.getCachedZipPath(config));
	}
	
	public static ModStructure inspectArchive(final Path zipPath) throws IOException {		
		try(ZipFile zipFile = new ZipFile(zipPath.toFile())){
			ZipNode root = ZipNode.fromZipFile(zipFile); // Get structure of zip
			
			// Find GameData Path within zip
			ZipNode gameData = getGameDataNode(root);
			gameData = gameData != null ? gameData : root;
			
			// Discover structure
			return new ModStructure(zipPath, gameData, getModules(gameData), getReadmeText(zipFile));
		}
	}
	
	public static String getReadmeText(final Config config, final Mod mod){
		try(ZipFile zipFile = new ZipFile(mod.getCachedZipPath(config).toFile())){
			return getReadmeText(zipFile);
		} catch (IOException e) {}
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
