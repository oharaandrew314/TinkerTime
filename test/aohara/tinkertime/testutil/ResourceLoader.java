package aohara.tinkertime.testutil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import aohara.common.tree.TreeNode;
import aohara.tinkertime.TinkerConfig;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.Crawler.Asset;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.models.ModStructure;

public class ResourceLoader {
	
	private static final TinkerConfig config = MockHelper.newConfig();
	
	public static Crawler<?> loadCrawler(ModStubs stub){
		return loadCrawler(stub, false);
	}
	
	public static Crawler<?> loadCrawler(ModStubs stub, boolean fallback){
		try {
			Crawler<?> crawler = MockHelper.newCrawlerFactory().getCrawler(stub.url, fallback);
			crawler.setAssetSelector(new StaticAssetSelector());
			return crawler;
		} catch (UnsupportedHostException e) {
			throw new RuntimeException(String.format("ModStub: %s has an unsupported URL", stub));
		}
	}
	
	public static Mod loadMod(ModStubs stub){
		try {
			return loadCrawler(stub).createMod();
		} catch (IOException e) {
			throw new RuntimeException("Error creating mod for stub: " + stub.name);
		}
	}
	
	public static Path getZipPath(Mod mod){
		Path path = mod.getCachedZipPath(config);
		if (path.toFile().exists()){
			return path;
		}
		throw new RuntimeException(String.format("Mod zip not found: %s", path));
	}
	
	public static Path getZipPath(ModStubs stub){
		return getZipPath(loadMod(stub));
	}
	
	public static ModStructure getStructure(ModStubs stub) throws IOException{
		return ModStructure.inspectArchive(getZipPath(stub));
	}
	
	public static TreeNode getModule(ModStructure struct, String moduleName){
		for (TreeNode module : struct.getModules()){
			if (module.getName().equals(moduleName)){
				return module;
			}
		}
		return null;
	}
	
	private static class StaticAssetSelector implements Crawler.AssetSelector {

		@Override
		public Asset selectAsset(String modName, Collection<Asset> assets) {
			return new ArrayList<>(assets).get(0);
		}
		
	}
}
