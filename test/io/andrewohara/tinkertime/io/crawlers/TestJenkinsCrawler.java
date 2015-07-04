package io.andrewohara.tinkertime.io.crawlers;

import io.andrewohara.tinkertime.io.crawlers.CrawlerFactory.UnsupportedHostException;
import io.andrewohara.tinkertime.models.mod.Mod;

import java.io.IOException;

import org.junit.Test;

public class TestJenkinsCrawler extends AbstractTestModCrawler {

	@Test
	public void testBuild1() throws IOException, UnsupportedHostException{
		testMod(testFixtures.getJenkinsModuleManager());
	}

	@Override
	protected Crawler<?> getCrawler(Mod mod) {
		return new JenkinsCrawler(mod.getUrl(), getJsonLoader());
	}
}
