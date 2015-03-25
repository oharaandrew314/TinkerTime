package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;

import org.junit.Test;

import aohara.tinkertime.crawlers.CrawlerFactory;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.crawlers.JenkinsCrawler;
import aohara.tinkertime.crawlers.pageLoaders.JsonLoader;

public class TestModuleManagerCrawler {
	
	private static JenkinsCrawler loadTestPage(int num) throws IOException, UnsupportedHostException {
		URL url = TestModuleManagerCrawler.class.getClassLoader().getResource(String.format("json/moduleManagerPage%d.json", num));
		return new JenkinsCrawler(url, new JsonLoader(), "Module Manager", new URL(CrawlerFactory.MODULE_MANAGER_ARTIFACT_DL_URL));
	}

	@Test
	public void testGetUpdatedOn() throws IOException, UnsupportedHostException {
		JenkinsCrawler crawler = loadTestPage(1);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(crawler.getUpdatedOn());
		
		assertEquals(2014, cal.get(Calendar.YEAR));
		assertEquals(Calendar.JULY, cal.get(Calendar.MONTH));
		assertEquals(19, cal.get(Calendar.DATE));
	}
	
	@Test
	public void testGetNewestFileName() throws IOException, UnsupportedHostException {
		JenkinsCrawler crawler = loadTestPage(1);
		
		assertEquals("ModuleManager.2.2.0.dll", crawler.getNewestFileName());
	}
	
	@Test
	public void testGetDownloadLink() throws IOException, UnsupportedHostException {
		JenkinsCrawler crawler = loadTestPage(1);
		
		String expectedUrl = (
			"https://ksp.sarbian.com/jenkins/job/ModuleManager/"
			+ "lastSuccessfulBuild/artifact/ModuleManager.2.2.0.dll");
		assertEquals(expectedUrl, crawler.getDownloadLink().toString());
	}
	
	@Test
	public void testIsSuccesfulForPassedBuild() throws IOException, UnsupportedHostException{
		JenkinsCrawler crawler = loadTestPage(1);
		assertTrue(crawler.isSuccesful());
	}
	
	@Test
	public void testIsSuccesfulForFailedBuild() throws IOException, UnsupportedHostException{
		JenkinsCrawler crawler = loadTestPage(2);
		assertFalse(crawler.isSuccesful());
	}
}
