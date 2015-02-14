package test.integration;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;

import org.junit.BeforeClass;
import org.junit.Test;

import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.CrawlerFactory;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;

/**
 * WARNING! THIS TEST REQUIRES AN INTERNET CONNECTION!
 *  
 * @author Andrew O'Hara
 */

public class TestDownloadLinkFormatting {
	
	@BeforeClass
	public static void setUpClass(){
		System.setProperty("http.agent", "TinkerTime Bot");
	}

	@Test
	public void tesEngineer() throws IOException, UnsupportedHostException{
		test("http://www.curse.com/ksp-mods/kerbal/220221-mechjeb");
	}
	
	@Test
	public void testB9() throws IOException, UnsupportedHostException{
		test("http://www.curse.com/ksp-mods/kerbal/220473-b9-aerospace-repack");
	}
	
	@Test
	public void testMechjeb() throws IOException, UnsupportedHostException{
		test("http://www.curse.com/ksp-mods/kerbal/220221-mechjeb");
	}
	
	@Test
	public void testOrbitalScience() throws IOException, UnsupportedHostException{
		test("http://www.curse.com/ksp-mods/kerbal/220208-dmagic-orbital-science");
	}
	
	private void test(String modUrl) throws IOException, UnsupportedHostException{
		Crawler<?> crawler = new CrawlerFactory().getCrawler(new URL(modUrl));
		assertTrue(crawler.getDownloadLink().openConnection().getContentLength() > 0);
	}
}
