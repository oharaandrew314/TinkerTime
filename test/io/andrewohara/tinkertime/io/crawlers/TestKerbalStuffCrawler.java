package io.andrewohara.tinkertime.io.crawlers;

import io.andrewohara.tinkertime.io.crawlers.Crawler;
import io.andrewohara.tinkertime.io.crawlers.CrawlerFactory.UnsupportedHostException;
import io.andrewohara.tinkertime.io.crawlers.KerbalStuffCrawler;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.io.IOException;

import org.junit.Test;

public class TestKerbalStuffCrawler extends AbstractTestModCrawler {

	@Test
	public void testRadialEngines() throws IOException, UnsupportedHostException {
		testMod(testFixtures.getKSRadialMounts());
	}

	@Test
	public void testTimeControl() throws IOException, UnsupportedHostException {
		testMod(testFixtures.getKSTimeControl());
	}

	@Test
	public void testBackgroundProcessing() throws IOException, UnsupportedHostException {
		testMod(testFixtures.getKSBackgroundProcessing());
	}

	@Override
	protected Crawler<?> getCrawler(Mod mod) {
		return new KerbalStuffCrawler(mod, getJsonLoader());
	}
}
