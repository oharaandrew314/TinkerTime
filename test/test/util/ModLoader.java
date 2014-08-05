package test.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import aohara.tinkertime.Config;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModStructure;
import aohara.tinkertime.models.ModStructure.Module;
import aohara.tinkertime.models.pages.ModPage;
import aohara.tinkertime.models.pages.PageFactory;

public class ModLoader {
	
	private static final Map<String, String> PAGE_URLS = new HashMap<>();
	public static final String
		ENGINEER = "Kerbal Engineer Redux",
		MECHJEB = "MechJeb",
		TESTMOD1 = "TestMod",
		TESTMOD2 = "TestMod2",
		ALARMCLOCK = "Kerbal Alarm Clock",
		NAVBALL = "Enhanced Navball",
		HOTROCKETS = "HotRockets",
		VISUALENHANCEMENTS = "EnvironmentalVisualEnhancements";
	
	static {
		PAGE_URLS.put(
			ENGINEER,
			"http://www.curse.com/ksp-mods/kerbal/220285-kerbal-engineer-redux"
		);
		PAGE_URLS.put(
			MECHJEB,
			"http://www.curse.com/ksp-mods/kerbal/220221-mechjeb"
		);
		PAGE_URLS.put(
			TESTMOD1,
			"http://www.curse.com/ksp-mods/kerbal/220285-kerbal-engineer-redux"
		);
		PAGE_URLS.put(
			TESTMOD2,
			"http://www.curse.com/ksp-mods/kerbal/220285-kerbal-engineer-redux"
		);
		PAGE_URLS.put(
			ALARMCLOCK,
			"http://www.curse.com/ksp-mods/kerbal/220289-kerbal-alarm-clock"
		);
		PAGE_URLS.put(
			NAVBALL,
			"http://www.curse.com/ksp-mods/kerbal/220469-enhanced-navball-v1-2"
		);
		PAGE_URLS.put(
			HOTROCKETS,
			"http://www.curse.com/ksp-mods/kerbal/220207-hotrockets-particle-fx-replacement"
		);
		PAGE_URLS.put(
			VISUALENHANCEMENTS,
			"https://github.com/rbray89/EnvironmentalVisualEnhancements/releases"
		);
	}
	
	public static String getUrl(String modName){
		return PAGE_URLS.get(modName);
	}

	public static ModPage getHtmlPage(String name){
		try {
			String url = PAGE_URLS.get(name);
			
			Element doc = Jsoup.parse(
				ModLoader.class.getClassLoader().getResourceAsStream(
					String.format("test/res/%s.html", name)
				), null, url);
					
			return PageFactory.loadModPage(doc, new URL(url));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static URL getZipUrl(String name){
		return ModLoader.class.getClassLoader().getResource(
			String.format("test/res/%s.zip", name)
		);
	}
	
	public static ModStructure getStructure(String name){
		try {
			return new ModStructure(Paths.get(getZipUrl(name).toURI()));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Module getModule(ModStructure struct, String moduleName){
		for (Module module : struct.getModules()){
			if (module.getName().equals(moduleName)){
				return module;
			}
		}
		return null;
	}
	
	public static Mod addMod(String name, Config config) throws Throwable {
		Mod mod = new Mod(getHtmlPage(name));
		try {
			FileUtils.copyURLToFile(
				getZipUrl(name),
				config.getModZipPath(mod).toFile()
			);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mod;
	}
}
