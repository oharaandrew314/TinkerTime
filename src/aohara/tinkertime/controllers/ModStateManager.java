package aohara.tinkertime.controllers;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import aohara.common.Listenable;
import aohara.common.selectorPanel.SelectorInterface;
import aohara.tinkertime.Config;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModStructure;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ModStateManager extends Listenable<SelectorInterface<Mod>>
		implements ModUpdateListener {
	
	private final Gson gson;
	private final Path modsPath;
	private final Type modsType = new TypeToken<Set<Mod>>() {}.getType();
	
	private final Set<Mod> modCache = new HashSet<>();
	private final Map<Mod, ModStructure> structureCache = new HashMap<>();
	
	public ModStateManager(Path modsPath){
		gson = new Gson();
		this.modsPath = modsPath;
	}
	
	private void updateListeners(Collection<Mod> mods){
		for (SelectorInterface<Mod> l : getListeners()){
			l.setDataSource(mods);
		}
	}
	
	private synchronized Set<Mod> loadMods(){
		try(FileReader reader = new FileReader(modsPath.toFile())){
			Set<Mod> mods = gson.fromJson(reader, modsType);
			if (mods != null){
				updateListeners(mods);
				return mods;
			}
		} catch (FileNotFoundException e){
			// No Action
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return new HashSet<Mod>();
	}
	
	private void saveMods(Set<Mod> mods){
		try(FileWriter writer = new FileWriter(modsPath.toFile())){
			gson.toJson(mods, modsType, writer);
			updateListeners(mods);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized Set<Mod> getMods(){
		if (modCache.isEmpty()){
			modCache.addAll(loadMods());
		}
		
		return new HashSet<Mod>(modCache);
	}
	
	public synchronized Map<Mod, ModStructure> getModStructures(Config config){
		if (structureCache.isEmpty()){
			for (Mod mod : getMods()){
				if (config.getModZipPath(mod).toFile().exists()){
					structureCache.put(mod, new ModStructure(mod, config));
				}
			}
		}
		return new HashMap<Mod, ModStructure>(structureCache);
	}
	
	public synchronized Set<ModStructure> getStructures(Config config){
		return new HashSet<ModStructure>(getModStructures(config).values());
	}

	@Override
	public synchronized void modUpdated(Mod mod, boolean deleted) {
		modCache.clear();
		structureCache.clear();
		
		Set<Mod> mods = loadMods();
		
		// Remove the old mod
		Set<Mod> toRemove = new HashSet<>();
		for (Mod m : mods){
			if (m.getName().equals(mod.getName())){
				toRemove.add(m);
			}
		}
		mods.removeAll(toRemove);
		
		// If the mod is being updated, add the new data
		if (!deleted){
			mods.add(mod);
		}
		
		saveMods(mods);
	}
}
