package io.andrewohara.tinkertime.testUtil;

import io.andrewohara.tinkertime.io.crawlers.Crawler.Asset;
import io.andrewohara.tinkertime.io.crawlers.Crawler.AssetSelector;

import java.util.ArrayList;
import java.util.Collection;

public class StaticAssetSelector implements AssetSelector {

	@Override
	public Asset selectAsset(String modName, Collection<Asset> assets) {
		return new ArrayList<>(assets).get(0);
	}

}
