package aohara.tinkertime.models;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.CrawlerFactory;

public class DefaultMods {
	
	private static final String[] DEPRECATED_DEFAULT_IDS = new String[]{ "ksp.sarbian.com" };
	
	private static Collection<Mod> getDefaults() {
		Collection<Mod> defaults = new LinkedList<>();
		
		// Add Module Manager to Defaults
		try {
			URL moduleManagerUrl = CrawlerFactory.getModuleManagerUrl();
			 defaults.add(new Mod(
				Crawler.urlToId(moduleManagerUrl),
				"ModuleManager", null, null, moduleManagerUrl, null, null, null
			));
		} catch (MalformedURLException e){
			throw new RuntimeException(e);
		}
		
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
