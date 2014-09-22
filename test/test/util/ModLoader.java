package test.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

import aohara.tinkertime.Config;
import aohara.tinkertime.content.ArchiveInspector;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModStructure;
import aohara.tinkertime.models.Module;

public class ModLoader {
	
	public static Mod loadMod(ModStubs stub){
		try {
			return new MockCrawlerFactory().getModCrawler(stub.url).createMod();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static URL getZipUrl(ModStubs stub){
		return ModLoader.class.getClassLoader().getResource(
			String.format("test/res/zips/%s.zip", stub.name)
		);
	}
	
	public static Path getZipPath(ModStubs stub){
		try {
			return Paths.get(getZipUrl(stub).toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static ModStructure getStructure(ModStubs stub){
		return ArchiveInspector.inspectArchive(getZipPath(stub));
	}
	
	public static Module getModule(ModStructure struct, String moduleName){
		for (Module module : struct.getModules()){
			if (module.getName().equals(moduleName)){
				return module;
			}
		}
		return null;
	}
	
	public static Mod addMod(ModStubs stub, Config config) throws Throwable {
		Mod mod = new MockCrawlerFactory().getModCrawler(stub.url).createMod();
		try {
			FileUtils.copyURLToFile(
				getZipUrl(stub),
				config.getModZipPath(mod).toFile()
			);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mod;
	}
}
