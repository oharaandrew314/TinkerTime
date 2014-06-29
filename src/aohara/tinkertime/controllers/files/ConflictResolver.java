package aohara.tinkertime.controllers.files;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import aohara.tinkertime.config.Config;
import aohara.tinkertime.controllers.ModStateManager;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModStructure;
import aohara.tinkertime.models.ModStructure.Module;

public abstract class ConflictResolver {
	
	public enum Resolution { Skip, Overwrite };
	
	private final Config config;
	private final ModStateManager sm;
	
	public ConflictResolver(Config config, ModStateManager sm){
		this.config = config;
		this.sm = sm;
	}
	
	public abstract Resolution getResolution(Module module, Mod mod);
	
	public Set<Mod> getDependentMods(Module module){
		Set<Mod> mods = new HashSet<Mod>();
		
		for (Entry<Mod, ModStructure> entry : sm.getModStructures(config).entrySet()){
			if (entry.getValue().usesModule(module)){
				mods.add(entry.getKey());
			}
		}
		return mods;
	}

}
