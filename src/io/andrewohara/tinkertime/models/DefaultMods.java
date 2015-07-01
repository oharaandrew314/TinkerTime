package io.andrewohara.tinkertime.models;

import io.andrewohara.tinkertime.io.crawlers.CrawlerFactory;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;


public class DefaultMods {

	public static URL getModuleManagerUrl(){
		try {
			return new URL("https", CrawlerFactory.HOST_MODULE_MANAGER, "/jenkins/job/ModuleManager");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e); // Programming error if this occurs
		}
	}

	public static Collection<Mod> getDefaults(Installation installation) {
		Mod mod = new Mod(getModuleManagerUrl(), installation);
		return Collections.singleton(mod);
	}
}
