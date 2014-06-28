package aohara.tinkertime.controllers;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import aohara.tinkertime.models.Mod;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ModStateManager implements ModUpdateListener {
	
	private final Gson gson;
	private final Path modsPath;
	private final Type modsType = new TypeToken<Set<Mod>>() {}.getType();
	
	public ModStateManager(Path modsPath){
		gson = new Gson();
		this.modsPath = modsPath;
	}
	
	public Set<Mod> getMods(){
		try(FileReader reader = new FileReader(modsPath.toFile())){
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
	
	private void saveMods(Set<Mod> mods){		
		try(FileWriter writer = new FileWriter(modsPath.toFile())){
			gson.toJson(mods, modsType, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void modUpdated(Mod mod) {
		Set<Mod> mods = getMods();
		if (mods.contains(mod)){
			mods.remove(mod);
		}
		mods.add(mod);
		
		saveMods(mods);
	}	

}
