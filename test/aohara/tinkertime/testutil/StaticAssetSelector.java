package aohara.tinkertime.testutil;

import java.util.ArrayList;
import java.util.Collection;

import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.Crawler.Asset;

public class StaticAssetSelector implements Crawler.AssetSelector {

	@Override
	public Asset selectAsset(String modName, Collection<Asset> assets) {
		return new ArrayList<>(assets).get(0);
	}
	
}
