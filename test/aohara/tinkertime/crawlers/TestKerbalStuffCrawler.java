package aohara.tinkertime.crawlers;

import java.io.IOException;
import java.util.Calendar;

import org.junit.Test;

import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.testutil.ModStubs;

public class TestKerbalStuffCrawler extends AbstractTestModCrawler {
	
	@Test
	public void testRadialEngines() throws IOException, UnsupportedHostException {		
		compare(
			ModStubs.RadialEngines,
			"153",
			Calendar.getInstance().getTime(),
			"teejaye85",
			"Radial Engine Mounts by PanaTee Parts International v0.30.zip",
			"https://kerbalstuff.com/mod/153/Radial%20Engine%20Mounts%20by%"
			+ "20PanaTee%20Parts%20International/download/v0.30",
			"https://kerbalstuff.com/1ATqqe1TChQV.png",
			"0.24.2"
		);
	}
	
	@Test
	public void testTimeControl() throws IOException, UnsupportedHostException {
		compare(
			ModStubs.TimeControl,
			"21",
			Calendar.getInstance().getTime(),
			"Xaiier",
			"Time Control 13.2.zip",
			"https://kerbalstuff.com/mod/21/Time%20Control/download/13.2",
			"https://kerbalstuff.com/NSBC0_9jcVmk.png",
			"0.24.2"
		);
	}

}
