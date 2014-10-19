package aohara.tinkertime.controllers;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import aohara.common.Listenable;
import aohara.common.selectorPanel.SelectorInterface;
import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.FileUpdateListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Controller for Storing and Retrieving persistent mod state.
 * 
 * Any time a mod's information or state is updated, the updater must call
 * modUpdated as specified by the ModUpdateListener interface. 
 * 
 * @author Andrew O'Hara
 */
public class ModStateManager extends Listenable<SelectorInterface<Mod>>
		implements ModUpdateListener, FileUpdateListener {
	
	private final Gson gson;
	private final TinkerConfig config;
	private final Type modsType = new TypeToken<Set<Mod>>() {}.getType();
	
	private final Set<Mod> modCache = new HashSet<>();
	
	public ModStateManager(TinkerConfig config){
		gson = new Gson();
		this.config = config;
	}
	
	private synchronized Set<Mod> loadMods(){
		try(FileReader reader = new FileReader(config.getModsListPath().toFile())){
			Set<Mod> mods = gson.fromJson(reader, modsType);
			if (mods != null){
				return mods;
			}
		} catch (FileNotFoundException e){
			// No Action
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return new HashSet<Mod>();
	}
	
	public synchronized Set<Mod> getMods(){
		if (modCache.isEmpty()){
			modCache.addAll(loadMods());
			for (SelectorInterface<Mod> l : getListeners()){
				l.clear();
				for (Mod mod : modCache){
					l.addElement(mod);
				}
			}
		}
		return new HashSet<Mod>(modCache);
	}

	@Override
	public synchronized void modUpdated(Mod mod, boolean deleted) {
		// See if a mod needs to be removed
		
		Set<Mod> toRemove = new HashSet<>();
		for (Mod m : modCache){
			if (m.equals(mod)){
				toRemove.add(mod);
				for (SelectorInterface<Mod> l : getListeners()){
					l.removeElement(mod);
				}
			}
		}
		modCache.removeAll(toRemove);
		
		// Update Mod if it is not removed
		if (!deleted){
			modCache.add(mod);
			for (SelectorInterface<Mod> l : getListeners()){
				l.addElement(mod);
			}
		}
		
		// Save Mods
		try(FileWriter writer = new FileWriter(config.getModsListPath().toFile())){
			gson.toJson(modCache, modsType, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void setUpdateAvailable(URL pageUrl, String newestFileName) {
		for (Mod mod : getMods()){
			if (mod.getPageUrl().equals(pageUrl)){
				modUpdated(mod, false);
				break;
			}
		}
	}
}
