package test.integration;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import aohara.tinkertime.controllers.crawlers.CurseCrawler;

/**
 * WARNING! THIS TEST REQUIRES AN INTERNET CONNECTION!
 * THIS TEST COMMUNICATES WITH CURSE.COM TO VERIFY DOWNLOAD LINK FORMATTING
 * 
 * @author Andrew O'Hara
 *
 */
public class TestDownloadLinkFormatting {

	@Test
	public void tesEngineer() throws IOException{
		test("http://www.curse.com/ksp-mods/kerbal/220221-mechjeb");
	}
	
	@Test
	public void testB9() throws IOException{
		test("http://www.curse.com/ksp-mods/kerbal/220473-b9-aerospace-repack");
	}
	
	@Test
	public void testKolonization() throws IOException{
		test("http://www.curse.com/ksp-mods/kerbal/220668-modular-kolonization-system");
	}
	
	@Test
	public void testMechjeb() throws IOException{
		test("http://www.curse.com/ksp-mods/kerbal/220221-mechjeb");
	}
	
	@Test
	public void testOrbitalScience() throws IOException{
		test("http://www.curse.com/ksp-mods/kerbal/220208-dmagic-orbital-science");
	}
	
	private void test(String modUrl) throws IOException{
		CurseCrawler crawler = new CurseCrawler(new URL(modUrl));
		assertTrue(crawler.getDownloadLink().openConnection().getContentLength() > 0);
	}
}
