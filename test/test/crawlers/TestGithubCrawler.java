package test.crawlers;

import java.io.IOException;

import org.junit.Test;

import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import test.util.ModStubs;

public class TestGithubCrawler extends AbstractTestModCrawler {
	
	@Test
	public void testKerbalAlarmClock() throws IOException, UnsupportedHostException {
		compare(
			ModStubs.KerbalAlarmClock,
			"KerbalAlarmClock",
			getDate(2014, 12, 19),
			"TriggerAu",
			"KerbalAlarmClock_3.2.3.0.zip",
			"https://github.com/TriggerAu/KerbalAlarmClock/releases",
			null,
			null
		);
	}
	
	@Test
	public void testProceduralFairings() throws IOException, UnsupportedHostException {
		compare(
			ModStubs.ProceduralFairings,
			"ProceduralFairings",
			getDate(2014, 11, 17),
			"e-dog",
			"ProcFairings_3.11.zip",
			"https://github.com/e-dog/ProceduralFairings/releases",
			null,
			null
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
			"KSP-Stock-Bug-Fix-Modules",
			getDate(2015,0,7),
			"ClawKSP",
			"StockBugFixModules.v0.1.7d.zip",
			"https://github.com/ClawKSP/KSP-Stock-Bug-Fix-Modules/releases",
			null,
			null
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
			"ActiveTextureManagement",
			getDate(2014,11,17),
			"rbray89",
			"x64-Aggressive-Release.zip",
			"https://github.com/rbray89/ActiveTextureManagement/releases",
			null,
			null
		);
		
	}

}
