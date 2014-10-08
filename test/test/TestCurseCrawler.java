package test;

import java.io.IOException;

import org.junit.Test;

import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import test.util.ModStubs;

public class TestCurseCrawler extends AbstractTestModCrawler {

	@Test
	public void testMechjeb() throws IOException, UnsupportedHostException {		
		compare(
			ModStubs.Mechjeb,
			"220221-mechjeb",
			getDate(2014, 4, 6),
			"r4m0n",
			"MechJeb2-2.2.1.0.zip",
			"http://addons.curse.cursecdn.com/files/2201/514/MechJeb2-2.2.1.0.zip",
			"http://media-curse.cursecdn.com/attachments/thumbnails/"
			+ "110/952/190/130/18b6dda728d709420f4b8959464e32ba.png",
			"0.24.2"
		);
	}
	
	@Test
	public void testEngineer() throws IOException, UnsupportedHostException {		
		compare(
			ModStubs.Engineer,
			"220285-kerbal-engineer-redux",
			getDate(2014, 4, 12),
			"cybutek",
			"Engineer_Redux_v0.6.2.4.zip",
			"http://addons.curse.cursecdn.com/files/2201/929/"
			+ "Engineer%20Redux%20v0.6.2.4.zip",
			"http://media-curse.cursecdn.com/attachments/thumbnails/111/144/"
			+ "190/130/46177821cf553bb4c8a15189f36d77f4.png",
			"0.24.2"
		);
	}

}
