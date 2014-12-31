package aohara.tinkertime.controllers;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import aohara.common.Listenable;
import aohara.common.selectorPanel.SelectorInterface;
import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.models.DefaultMods;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.FileUpdateListener;
import aohara.tinkertime.views.FileChoosers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Controller for Storing and Retrieving persistent mod state.
 * 
 * Any time a mod's information or state is updated, the updater must call
 * modUpdated as specified by the ModUpdateListener interface. 
 * 
 * @author Andrew O'Hara
 */
public class ModLoader extends Listenable<SelectorInterface<Mod>>
		implements ModUpdateListener, FileUpdateListener {
	
	private final Gson gson;
	private final TinkerConfig config;
	private final Type modsType = new TypeToken<Set<Mod>>() {}.getType();
	private final Set<Mod> modCache = new HashSet<>();
	
	// -- Initializers ----------------------------------------
	
	public ModLoader(TinkerConfig config, Gson gson){
		this.config = config;
		this.gson = gson;
	}
	
	public static ModLoader create(TinkerConfig config){
		return new ModLoader(config, new GsonBuilder().setPrettyPrinting().create());
	}
	
	public synchronized void init(ModManager mm){
		for (SelectorInterface<Mod> l : getListeners()){
			l.clear();
		}
		importMods(config.getModsListPath(), mm);
	}
	
	//-- Public Methods ----------------------------------------
	
	public synchronized Set<Mod> getMods(){
		return new HashSet<Mod>(modCache);
	}
	
	/**
	 * Call when a mod has been updated.
	 * 
	 * Updates the persistent mod data, and refreshes the mod views.
	 */
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
	
	/**
	 * Call when a mod has been deleted.
	 * 
	 * Deleted the persistent mod data, and removes from mod views.
	 */
	@Override
	public synchronized void modDeleted(Mod mod){
		modCache.remove(mod);
		for (SelectorInterface<Mod> l : getListeners()){
			l.removeElement(mod);
		}
		saveMods(modCache, config.getModsListPath());
	}
	
	public synchronized void exportEnabledMods(Path path){
		Set<Mod> toExport = new HashSet<>();
		for (Mod mod : modCache){
			if (mod.isEnabled()){
				toExport.add(mod);
			}
		}
		saveMods(toExport, path);
	}
	
	public synchronized void importMods(Path path, ModManager mm){
		for (Mod mod : loadMods(path, mm)){
			for (SelectorInterface<Mod> l : getListeners()){
				if (modCache.contains(mod)){
					l.removeElement(mod);
				}
				l.addElement(mod);
			}
			modCache.add(mod);
		}
	}

	@Override
	public synchronized void setUpdateAvailable(Crawler<?> crawler) {
		for (Mod mod : modCache){
			if (mod.isUpdateable() && mod.id.equals(crawler.generateId())){
				mod.setUpdateAvailable(crawler);
				break;
			}
		}
	}
	
	// -- Private Methods ----------------------------------------
	
	private Set<Mod> loadMods(Path path, ModManager mm){
		Set<Mod> mods = new HashSet<>();
		
		try(FileReader reader = new FileReader(path.toFile())){
			// Try to load mods from file
			Set<Mod> newMods = gson.fromJson(reader, modsType);
			for (Mod newMod : newMods){
				// If mod is updateable, or if the local zip file is available, add mod
				if (newMod.isUpdateable() || newMod.isDownloaded(config) || trySatisfyLocalFiles(newMod, mm)){
					mods.add(newMod);
				}
			}
		} catch (FileNotFoundException e){
			// No Action
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		mods.addAll(DefaultMods.getDefaults());
		return mods;
	}
	
	private boolean trySatisfyLocalFiles(Mod mod, ModManager mm){
		if (JOptionPane.showConfirmDialog(
			null,
			mod.getName() + " does not have an update url.\n"
			+ "Would you like to select a zip file to use?",
			"Import Local Mod?",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE
		) == JOptionPane.YES_OPTION){
			Path zipPath = FileChoosers.chooseModZip();
			if (zipPath != null){
				mm.addModZip(zipPath);
			}
		}
		return false;
	}
	
	private void saveMods(Set<Mod> mods, Path path){
		try(FileWriter writer = new FileWriter(path.toFile())){
			gson.toJson(mods, modsType, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
