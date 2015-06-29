package aohara.tinkertime.models;

import java.util.Collection;
import java.util.LinkedList;

import aohara.tinkertime.crawlers.CrawlerFactory;

// TODO Remove me whe migrations added
public class DefaultMods {

	private static Collection<Mod> getDefaults() {
		Collection<Mod> defaults = new LinkedList<>();

		// Add Module Manager to Defaults
		defaults.add(new Mod(
				Mod.MODULE_MANAGER_ID,
				"ModuleManager", null, null, CrawlerFactory.getModuleManagerUrl(),
				null, null, null
				));

		return defaults;
	}

	public static void ensureDefaults(Collection<Mod> mods) {
		// TODO convert to DB migration
		// Ensure Existence of Default Mods
		for (Mod builtIn : getDefaults()) {
			if (!mods.contains(builtIn)) {
				mods.add(builtIn);
			}
		}
	}

	public static boolean isBuiltIn(Mod mod) {
		return getDefaults().contains(mod);
	}
}
