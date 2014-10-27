package test.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;

import thirdParty.ZipNode;
import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModStructure;

public class ModLoader {
	
	public static MockMod loadMod(ModStubs stub) throws UnsupportedHostException{
		try {
			Crawler<?> crawler = new MockCrawlerFactory().getCrawler(stub.url);
			Mod mod = new Mod(
				crawler.generateId(), crawler.getName(), crawler.getNewestFileName(),
				crawler.getCreator(), crawler.getImageUrl(), crawler.getPageUrl(),
				crawler.getUpdatedOn() != null ? crawler.getUpdatedOn() : Calendar.getInstance().getTime(),
				crawler.getSupportedVersion()
			);
			return new MockMod(mod);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static URL getZipUrl(String modName){
		return ModLoader.class.getClassLoader().getResource(
			String.format("zips/%s.zip", modName)
		);
	}
	
	public static Path getZipPath(String modName){
		try {
			return Paths.get(getZipUrl(modName).toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static ModStructure getStructure(ModStubs stub) throws IOException{
		return ModStructure.inspectArchive(getZipPath(stub.name));
	}
	
	public static ZipNode getModule(ModStructure struct, String moduleName){
		for (ZipNode module : struct.getModules()){
			if (module.getName().equals(moduleName)){
				return module;
			}
		}
		return null;
	}
	
	public static class MockMod extends Mod {
		
		private boolean downloaded = false;

		public MockMod(Mod mod) {
			super(
				mod.id,
				mod.getName(),
				mod.getNewestFileName(),
				mod.getCreator(),
				mod.getImageUrl(),
				mod.getPageUrl(),
				mod.getUpdatedOn(),
				mod.getSupportedVersion()
			);
		}
		
		public void setDownloaded(boolean downloaded){
			this.downloaded = downloaded;
		}
		
		@Override
		public Path getCachedZipPath(TinkerConfig config){
			return downloaded ? ModLoader.getZipPath(getName()) : Paths.get("/");
		}
	}
}
