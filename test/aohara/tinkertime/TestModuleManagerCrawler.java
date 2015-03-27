package aohara.tinkertime;

import java.io.IOException;

import org.junit.Test;

import aohara.tinkertime.crawlers.AbstractTestModCrawler;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.testutil.ModStubs;

public class TestModuleManagerCrawler extends AbstractTestModCrawler {
	
	@Test
	public void testBuild1() throws IOException, UnsupportedHostException{
		ModStubs stub = ModStubs.ModuleManagerBuild1;
		String newestFilename = "ModuleManager.2.5.9.dll";
		compare(
				stub,
				"moduleManagerPage1",
				getDate(2015, 1, 23),
				"ksp.sarbian.com",
				newestFilename,
				String.format("%s/lastSuccesfulBuild/artifact/%s", stub.url, newestFilename),
				null,
				null
		);
	}
}
