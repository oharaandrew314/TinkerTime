package test.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;

import aohara.tinkertime.models.ModPage;
import aohara.tinkertime.models.ModStructure;

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
	
	public static ModStructure getStructure(String name){
		URL url = ModLoader.class.getClassLoader().getResource(
			String.format("test/res/%s.zip", name)
		);
		try {
			return new ModStructure(Paths.get(url.toURI()));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}
	

}
