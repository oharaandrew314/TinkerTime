package io.andrewohara.tinkertime.io.crawlers;

import io.andrewohara.tinkertime.io.crawlers.CrawlerFactory.UnsupportedHostException;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.io.IOException;

import org.junit.Test;

public class TestCurseCrawler extends AbstractTestModCrawler {

	@Test
	public void testMechjeb() throws IOException, UnsupportedHostException {
		testMod(testFixtures.getCurseMechjeb());
	}

	@Test
	public void testEngineer() throws IOException, UnsupportedHostException {
		testMod(testFixtures.getCurseEngineerMod());
	}

	@Test
	public void testHotRockets() throws IOException, UnsupportedHostException {
		testMod(testFixtures.getCurseHotRockets());
	}

	@Override
	protected Crawler<?> getCrawler(Mod mod) {
		return new CurseCrawler(mod.getUrl(), getDocLoader());
	}

}
