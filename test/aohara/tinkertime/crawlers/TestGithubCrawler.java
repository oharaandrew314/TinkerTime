package aohara.tinkertime.crawlers;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.testutil.ModStubs;

public class TestGithubCrawler extends AbstractTestModCrawler {
	
	@Test
	public void testKerbalAlarmClock() throws IOException, UnsupportedHostException {
		compare(
			ModStubs.KerbalAlarmClock,
			"api.github.com-repos-TriggerAu-KerbalAlarmClock",
			getDate(2014, 12, 19),
			"TriggerAu",
			"KerbalAlarmClock_3.2.3.0.zip",
			"api.github.com/repos/TriggerAu/KerbalAlarmClock/releases",
			null,
			null,
			"3.2.3.0"
		);
	}
	
	@Test
	public void testProceduralFairings() throws IOException, UnsupportedHostException {
		compare(
			ModStubs.ProceduralFairings,
			"api.github.com-repos-e-dog-ProceduralFairings",
			getDate(2014, 11, 17),
			"e-dog",
			"ProcFairings_3.11.zip",
			"api.github.com/repos/e-dog/ProceduralFairings/releases",
			null,
			null,
			"3.11"
		);
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
		compare(
			ModStubs.StockFixes,
			"api.github.com-repos-ClawKSP-KSP-Stock-Bug-Fix-Modules",
			getDate(2015,0,7),
			"ClawKSP",
			"StockBugFixModules.v0.1.7d.zip",
			"api.github.com/repos/ClawKSP/KSP-Stock-Bug-Fix-Modules/releases",
			null,
			null,
			"0.1.7"
		);
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
		compare(
			ModStubs.ActiveTextureManagement,
			"api.github.com-repos-rbray89-ActiveTextureManagement",
			getDate(2014,11,17),
			"rbray89",
			"x64-Aggressive-Release.zip",
			"api.github.com-repos-rbray89-ActiveTextureManagement",
			null,
			null,
			"4.3"
		);
		
	}

	@Override
	protected Crawler<?> getCrawler(URL url) {
		return new GithubCrawler(url, getJsonLoader());
	}

}
