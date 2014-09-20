package aohara.tinkertime.controllers;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import aohara.common.Listenable;
import aohara.common.selectorPanel.SelectorInterface;
import aohara.tinkertime.content.ImageCache;
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
	private final Path modsPath;
	private final Type modsType = new TypeToken<Set<Mod>>() {}.getType();
	private final ImageCache imageCache;
	
	private final Set<Mod> modCache = new HashSet<>();
	
	public ModStateManager(Path modsPath, ImageCache imageCache){
		gson = new Gson();
		this.modsPath = modsPath;
		this.imageCache = imageCache;
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

	@Override
	public synchronized void modUpdated(Mod mod, boolean deleted) {
		modCache.clear();
		
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
		imageCache.modUpdated(mod, deleted);
	}

	@Override
	public void setUpdateAvailable(URL pageUrl, String newestFileName) {
		for (Mod mod : getMods()){
			if (mod.getPageUrl().equals(pageUrl)){
				modUpdated(mod, false);
				break;
			}
		}
	}
}
