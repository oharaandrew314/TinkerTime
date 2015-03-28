package aohara.tinkertime.crawlers;

import java.io.IOException;
import java.util.Date;

import org.junit.Test;

import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.testutil.ModStubs;

public abstract class AbstractTestGithubCrawler extends AbstractTestModCrawler {
	
	protected abstract boolean isTestingFallbackCrawler();
	
	@Override
	protected void compare(
		ModStubs stub, String id, Date updatedOn, String creator,
		String newestFile, String downloadLink, String imageLink,
		String kspVersion, String version
	) throws IOException, UnsupportedHostException {
		compare(stub, id, updatedOn, creator, newestFile, downloadLink, imageLink, kspVersion, version, isTestingFallbackCrawler());
	}
	
	@Test
	public void testKerbalAlarmClock() throws IOException, UnsupportedHostException {
		compare(
			ModStubs.KerbalAlarmClock,
			"github.com-TriggerAu-KerbalAlarmClock",
			getDate(2014, 12, 19),
			"TriggerAu",
			"KerbalAlarmClock_3.2.3.0.zip",
			"https://github.com/TriggerAu/KerbalAlarmClock/releases",
			null,
			null,
			"3.2.30"
		);
	}
	
	@Test
	public void testProceduralFairings() throws IOException, UnsupportedHostException {
		compare(
			ModStubs.ProceduralFairings,
			"github.com-e-dog-ProceduralFairings",
			getDate(2014, 11, 17),
			"e-dog",
			"ProcFairings_3.11.zip",
			"https://github.com/e-dog/ProceduralFairings/releases",
			null,
			null,
			"3.11.0"
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
			"github.com-ClawKSP-KSP-Stock-Bug-Fix-Modules",
			getDate(2015,0,7),
			"ClawKSP",
			"StockBugFixModules.v0.1.7d.zip",
			"https://github.com/ClawKSP/KSP-Stock-Bug-Fix-Modules/releases",
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
			"github.com-rbray89-ActiveTextureManagement",
			getDate(2014,11,17),
			"rbray89",
			"x64-Aggressive-Release.zip",
			"https://github.com/rbray89/ActiveTextureManagement/releases",
			null,
			null,
			"4.3.0"
		);
		
	}

}
