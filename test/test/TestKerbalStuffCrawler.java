package test;

import java.io.IOException;

import org.junit.Test;

import test.util.ModStubs;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;

public class TestKerbalStuffCrawler extends AbstractTestModCrawler {
	
	@Test
	public void testRadialEngines() throws IOException, UnsupportedHostException {		
		compare(
			ModStubs.RadialEngines,
			null,
			"teejaye85",
			"Radial Engine Mounts by PanaTee Parts International v0.30.zip",
			"https://kerbalstuff.com/mod/153/Radial%20Engine%20Mounts%20by%"
			+ "20PanaTee%20Parts%20International/download/v0.30",
			"https://cdn.mediacru.sh/1ATqqe1TChQV.png",
			"0.24.2"
		);
	}
	
	@Test
	public void testTimeControl() throws IOException, UnsupportedHostException {
		compare(
			ModStubs.TimeControl,
			null,
			"Xaiier",
			"Time Control 13.2.zip",
			"https://kerbalstuff.com/mod/21/Time%20Control/download/13.2",
			"https://cdn.mediacru.sh/NSBC0_9jcVmk.png",
			"0.24.2"
		);
	}

}
