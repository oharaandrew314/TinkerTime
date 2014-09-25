package test;

import java.io.IOException;

import org.junit.Test;

import aohara.tinkertime.controllers.crawlers.CrawlerFactory.UnsupportedHostException;
import test.util.ModStubs;

public class TestGithubCrawler extends AbstractTestModCrawler {

	@Test
	public void testVisualEnhancements() throws IOException, UnsupportedHostException{
		compare(
			ModStubs.Eve,
			getDate(2014, 8, 5),
			"rbray89",
			"EnvironmentalVisualEnhancements-7-4-LR.zip",
			"https://github.com/rbray89/EnvironmentalVisualEnhancements/releases/download/Release-7-4/EnvironmentalVisualEnhancements-7-4.zip",
			"https://avatars2.githubusercontent.com/u/1095572?v=2&s=40"
		);
	}
	
	@Test
	public void testProceduralFairings() throws IOException, UnsupportedHostException {
		compare(
			ModStubs.ProceduralFairings,
			getDate(2014, 7, 3),
			"e-dog",
			"ProcFairings_3.09.zip",
			"https://github.com/e-dog/ProceduralFairings/releases",
			"https://avatars0.githubusercontent.com/u/5958584?v=2&s=40"
		);
	}

}
