package aohara.tinkertime.models;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

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
	private final Set<Module> modules;
	public final Path zipPath;
	
	public ModStructure(Path zipPath, Set<Module> modules, String readmeText){
		this.zipPath = zipPath;
		this.modules = modules;
		this.readmeText = readmeText;
	}
	
	public boolean usesModule(Module module){
		for (Module m : modules){
			if (m.getName().equals(module.getName())){
				return true;
			}
		}
		return false;
	}
	
	public Set<Module> getModules(){
		return new HashSet<Module>(modules);
	}
}
