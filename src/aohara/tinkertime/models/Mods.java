package aohara.tinkertime.models;

import java.util.ArrayList;
import java.util.Collection;

public class Mods {
	
	private final Collection<Mod> mods;
	
	public Mods(){
		mods = new ArrayList<>();
	}
	
	public Collection<Mod> getMods(){
		return new ArrayList<Mod>(mods);
	}
	
	public Mod addMod(Mod mod){
		mods.add(mod);
		return mod;
	}
	
	public Mod removeMod(Mod mod){
		return mods.remove(mod) ? mod : null;
	}

}
