package test.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;

import aohara.tinkertime.config.Config;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModPage;
import aohara.tinkertime.models.ModStructure;
import aohara.tinkertime.models.ModStructure.Module;

public class ModLoader {
	
	private static final Map<String, String> PAGE_URLS = new HashMap<>();
	public static final String
		ENGINEER = "Kerbal Engineer Redux",
		MECHJEB = "MechJeb",
		TESTMOD1 = "TestMod",
		TESTMOD2 = "TestMod2";
	
	static {
		PAGE_URLS.put(
			"Kerbal Engineer Redux",
			"http://www.curse.com/ksp-mods/kerbal/220285-kerbal-engineer-redux"
		);
		PAGE_URLS.put(
			"MechJeb",
			"http://www.curse.com/ksp-mods/kerbal/220221-mechjeb"
		);
		PAGE_URLS.put(
			"TestMod",
			"http://www.curse.com/ksp-mods/kerbal/220285-kerbal-engineer-redux"
		);
		PAGE_URLS.put(
			"TestMod2",
			"http://www.curse.com/ksp-mods/kerbal/220285-kerbal-engineer-redux"
		);
	}

	public static ModPage getPage(String name){
		try {
			return new ModPage(Jsoup.parse(
				ModLoader.class.getClassLoader().getResourceAsStream(
					String.format("test/res/%s.html", name)
				),
				null,
				PAGE_URLS.get(name)
			));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static URL getZipUrl(String name){
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
		ModPage mod = getPage(name);
		try {
			FileUtils.copyURLToFile(
				getZipUrl(name),
				config.getModZipPath(mod).toFile()
			);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Mod(mod);
	}
}
