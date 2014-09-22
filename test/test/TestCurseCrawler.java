package test;

import java.io.IOException;

import org.junit.Test;

import test.util.ModStubs;

public class TestCurseCrawler extends AbstractTestModCrawler {

	@Test
	public void testMechjeb() throws IOException {		
		compare(
			ModStubs.Mechjeb,
			getDate(2014, 4, 6),
			"r4m0n",
			"MechJeb2-2.2.1.0.zip",
			"http://addons.curse.cursecdn.com/files/2201/514/MechJeb2-2.2.1.0.zip",
			"http://media-curse.cursecdn.com/attachments/thumbnails/"
			+ "110/952/190/130/18b6dda728d709420f4b8959464e32ba.png"
		);
	}
	
	@Test
	public void testEngineer() throws IOException {		
		compare(
			ModStubs.Engineer,
			getDate(2014, 4, 12),
			"cybutek",
			"Engineer_Redux_v0.6.2.4.zip",
			"http://addons.curse.cursecdn.com/files/2201/929/"
			+ "Engineer%20Redux%20v0.6.2.4.zip",
			"http://media-curse.cursecdn.com/attachments/thumbnails/111/144/"
			+ "190/130/46177821cf553bb4c8a15189f36d77f4.png"			
		);
	}

}
