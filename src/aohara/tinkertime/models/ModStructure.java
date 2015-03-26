package aohara.tinkertime.models;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

import aohara.common.tree.FileNode;
import aohara.common.tree.TreeNode;
import aohara.common.tree.zip.ZipTreeBuilder;
import aohara.tinkertime.TinkerConfig;

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
	
	private final Set<TreeNode> modules;
	
	private ModStructure(Set<TreeNode> modules){
		this.modules = modules;
	}
	
	public boolean usesModule(TreeNode module){
		return modules.contains(module);
	}
	
	public Set<TreeNode> getModules(){
		return new HashSet<TreeNode>(modules);
	}
	
	public Set<String> getModuleNames(){
		Set<String> moduleNames = new LinkedHashSet<>();
		for (TreeNode module : getModules()){
			moduleNames.add(module.getName());
		}
		return moduleNames;
	}
	
	// Factory Methods
	
	public static ModStructure inspectArchive(TinkerConfig config, Mod mod) throws IOException {
		return inspectArchive(mod.getCachedZipPath(config));
	}
	
	public static ModStructure inspectArchive(final Path zipPath) throws IOException {
		if (!zipPath.toString().endsWith(".zip")){
			Set<TreeNode> modules = new HashSet<>();
			modules.add(new FileNode(zipPath.getFileName().toString()));
			return new ModStructure(modules);
		}
		
		
		TreeNode root = new ZipTreeBuilder(zipPath).process();
		
		// Find GameData Path within zip
		TreeNode gameData = getGameDataNode(root);
		gameData = gameData != null ? gameData : root;
		
		// Discover structure
		return new ModStructure(getModules(gameData));
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
				try(StringWriter writer = new StringWriter(); InputStream is = zipFile.getInputStream(entry)){
					IOUtils.copy(is, writer);
					return writer.toString();
				} catch (IOException e) {}
			}
		}
		return null;
	}
	
	private static TreeNode getGameDataNode(final TreeNode zipNode){
		if (zipNode.getName().toLowerCase().equals("gamedata")){
			return zipNode;
		} else {
			for (TreeNode child : zipNode.getChildren()){
				TreeNode result = getGameDataNode(child);
				if (result != null){
					return result;
				}
			}
		}
		return null;
	}

	private static Set<TreeNode> getModules(final TreeNode gameDataNode){
		Set<TreeNode> modules = new HashSet<>();
		
		for (TreeNode module : gameDataNode.getChildren()){
			if (module.isDir()){
				module.makeRoot();
				modules.add(module);
			}
		}
		
		return modules;
	}
}
