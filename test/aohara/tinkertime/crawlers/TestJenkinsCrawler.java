package aohara.tinkertime.crawlers;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.testutil.ModStubs;

public class TestJenkinsCrawler extends AbstractTestModCrawler {
	
	@Test
	public void testBuild1() throws IOException, UnsupportedHostException{
		ModStubs stub = ModStubs.ModuleManagerBuild1;
		String newestFilename = "ModuleManager.2.5.12.dll";
		compare(
				stub,
				"ksp.sarbian.com-jenkins-job-moduleManagerPage1-lastSuccessfulBuild-api-json",
				getDate(2015, 1, 23),
				"ksp.sarbian.com",
				newestFilename,
				String.format("%s/lastSuccesfulBuild/artifact/%s", stub.url, newestFilename),
				null,
				null,
				"2.5.12"
		);
	}

	@Override
	protected Crawler<?> getCrawler(URL url) {
		return new JenkinsCrawler(url, getJsonLoader());
	}
}
