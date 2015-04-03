package aohara.tinkertime.models;

import java.util.Collection;
import java.util.LinkedList;

import aohara.tinkertime.crawlers.CrawlerFactory;

public class DefaultMods {

	private static Collection<Mod> getDefaults() {
		Collection<Mod> defaults = new LinkedList<>();

		// Add Module Manager to Defaults
		defaults.add(new Mod(
			"ksp.sarbian.com-jenkins-job-ModuleManager-lastSuccessfulBuild-api-json",
			"ModuleManager", null, null, CrawlerFactory.getModuleManagerUrl(),
			null, null, null
		));

		return defaults;
	}

	public static void ensureDefaults(Collection<Mod> mods) {
		// Ensure Existence of Default Mods
		for (Mod builtIn : getDefaults()) {
			if (!mods.contains(builtIn)) {
				mods.add(builtIn);
			}
		}
	}

	public static boolean isBuiltIn(Mod mod) {
		for (Mod builtIn : getDefaults()) {
			if (builtIn.id.equals(mod.id)) {
				return true;
			}
		}
		return false;
	}
}
