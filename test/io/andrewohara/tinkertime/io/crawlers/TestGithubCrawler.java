package io.andrewohara.tinkertime.io.crawlers;

import io.andrewohara.tinkertime.io.crawlers.Crawler;
import io.andrewohara.tinkertime.io.crawlers.CrawlerFactory.UnsupportedHostException;
import io.andrewohara.tinkertime.io.crawlers.GithubCrawler;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.io.IOException;

import org.junit.Test;

public class TestGithubCrawler extends AbstractTestModCrawler {

	@Test
	public void testKerbalAlarmClock() throws IOException, UnsupportedHostException {
		testMod(testFixtures.getGithubKerbalAlarmClock());
	}

	@Test
	public void testProceduralFairings() throws IOException, UnsupportedHostException {
		testMod(testFixtures.getGithubProceduralFairings());
	}

	/**
	 * Test fix for https://github.com/oharaandrew314/TinkerTime/issues/187
	 *
	 * Tests the case where the latest release does not have any user-uploaded
	 * assets.  The crawler should instead look for the latest release that does
	 * have user-uploaded assets.
	 *
	 * @throws IOException
	 * @throws UnsupportedHostException
	 */
	@Test
	public void testStockFixes() throws IOException, UnsupportedHostException {
		testMod(testFixtures.getGithubStockFixes());
	}

	/**
	 * Test fix for https://github.com/oharaandrew314/TinkerTime/issues/187
	 *
	 * Since the latest release element class is no longer used to identify the
	 * latest release, this test ensures that even if the latest release is a
	 * pre-release, it is not used.
	 *
	 * @throws IOException
	 * @throws UnsupportedHostException
	 */
	@Test
	public void testDontGetPrereleaseIfLatestRelease() throws IOException, UnsupportedHostException {
		testMod(testFixtures.getGithubActiveTextureManagement());

	}

	@Override
	protected Crawler<?> getCrawler(Mod mod) {
		return new GithubCrawler(mod, getJsonLoader());
	}

}
