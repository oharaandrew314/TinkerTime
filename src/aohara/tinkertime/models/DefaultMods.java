package aohara.tinkertime.models;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import aohara.tinkertime.crawlers.CrawlerFactory;

public class DefaultMods {
	
	private static final String[] DEPRECATED_DEFAULT_IDS = new String[]{ "ksp.sarbian.com" };
	
	private static Collection<Mod> getDefaults() {
		Collection<Mod> defaults = new LinkedList<>();
		
		// Add Module Manager to Defaults
		 defaults.add(new Mod(
			"ModuleManager", "Module Manager", null, null,
			CrawlerFactory.getModuleManagerUrl(), null, null
		));
		
		return defaults;
	}
	
	public static void ensureDefaults(Collection<Mod> mods){
		// Remove Deprecated Default Mods
		Collection<String> deprecatedIds = Arrays.asList(DEPRECATED_DEFAULT_IDS);
		Collection<Mod> modsToBeDeleted = new LinkedList<>();
		for (Mod mod : mods){
			if (deprecatedIds.contains(mod.id)){
				modsToBeDeleted.add(mod);
			}
		}
		mods.removeAll(modsToBeDeleted);
		
		// Ensure Existence of Default Mods
		for (Mod builtIn : getDefaults()){
			if (!mods.contains(builtIn)){
				mods.add(builtIn);
			}
		}
	}

	public static boolean isBuiltIn(Mod mod){
		for (Mod builtIn : getDefaults()){
			if (builtIn.id.equals(mod.id)){
				return true;
			}
		}
		return false;
	}
}
