package aohara.tinkertime.controllers;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import aohara.common.Listenable;
import aohara.common.selectorPanel.SelectorInterface;
import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.models.DefaultMods;
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
		Set<Mod> mods = new HashSet<>();
		try(FileReader reader = new FileReader(config.getModsListPath().toFile())){
			Set<Mod> loadedMods = gson.fromJson(reader, modsType);
			if(loadedMods != null){
				mods.addAll(loadedMods);
			}
		} catch (FileNotFoundException e){
			// No Action
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		mods.addAll(DefaultMods.getDefaults());
		return mods;
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
	public synchronized void modUpdated(Mod mod) {
		modCache.remove(mod);
		modCache.add(mod);
		
		for (SelectorInterface<Mod> l : getListeners()){
			l.removeElement(mod);
			l.addElement(mod);
		}
		
		saveMods(modCache, config.getModsListPath());
	}
	
	public synchronized void modDeleted(Mod mod){
		modCache.remove(mod);
		for (SelectorInterface<Mod> l : getListeners()){
			l.removeElement(mod);
		}
		saveMods(modCache, config.getModsListPath());
	}
	
	private void saveMods(Set<Mod> mods, Path path){
		try(FileWriter writer = new FileWriter(path.toFile())){
			gson.toJson(mods, modsType, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void exportEnabledMods(Path path){
		Set<Mod> toExport = new HashSet<>();
		for (Mod mod : getMods()){
			if (mod.isEnabled()){
				toExport.add(mod);
			}
		}
		saveMods(toExport, path);
	}

	@Override
	public synchronized void setUpdateAvailable(URL pageUrl, URL downloadLink, String newestFileName) {
		for (Mod mod : getMods()){
			if (mod.getPageUrl().equals(pageUrl)){
				modUpdated(mod);
				break;
			}
		}
	}
}
