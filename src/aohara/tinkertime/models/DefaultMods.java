package aohara.tinkertime.models;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;

import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.CrawlerFactory;
import aohara.tinkertime.crawlers.JenkinsCrawler;
import aohara.tinkertime.crawlers.pageLoaders.JsonLoader;

public class DefaultMods {
	
	private static Collection<Mod> getDefaults() {
		Collection<Mod> defaults = new LinkedList<>();
		
		// Add Module Manager to Defaults
		try {
			URL moduleManagerUrl = CrawlerFactory.getModuleManagerUrl();
			Crawler<?> crawler = new JenkinsCrawler(moduleManagerUrl, new JsonLoader());
			 defaults.add(new Mod(
				crawler.getId(),
				"ModuleManager", null, null, moduleManagerUrl, null, null, null
			));
		} catch (MalformedURLException e){
			throw new RuntimeException(e);
		}
		
		return defaults;
	}
	
	public static void ensureDefaults(Collection<Mod> mods){		
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
