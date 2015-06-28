package aohara.tinkertime.controllers;

import java.util.Collection;
import java.util.LinkedHashSet;

import aohara.tinkertime.models.Mod;

import com.google.inject.Singleton;

@Singleton
public class ModUpdateCoordinator {
	
	private final Collection<ModUpdateHandler> listeners = new LinkedHashSet<>();
	
	public void addHandler(ModUpdateHandler handler){
		listeners.add(handler);
	}
	
	public void clearMods(){
		for (ModUpdateHandler handler : listeners){
			handler.clear();
		}
	}

	public void modUpdated(Object source, Mod mod) {
		for (ModUpdateHandler handler : listeners){
			if (handler != source){
				handler.modUpdated(mod);
			}
		}
	}

	public void modDeleted(Object source, Mod mod) {
		for (ModUpdateHandler handler : listeners){
			if (handler != source){
				handler.modDeleted(mod);
			}
		}
	}

}
