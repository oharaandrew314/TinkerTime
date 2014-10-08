package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import aohara.tinkertime.crawlers.CrawlerFactory;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.crawlers.JenkinsCrawler;

public class TestModuleManagerCrawler {
	
	private static JenkinsCrawler loadTestPage(int num) throws IOException, UnsupportedHostException {
		URL url = new URL(String.format("file:/test/res/moduleManagerPage%d.json", num));
		return (JenkinsCrawler) new CrawlerFactory().getCrawler(url);
	}
	
	private Date getDateDelta(JenkinsCrawler crawler, int delta){
		try {
			Date date = (Date) crawler.getUpdatedOn().clone();
			date.setTime(date.getTime() + delta);
			return date;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
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
	public void testIsUpdateAvailableForPassedBuild() throws IOException, UnsupportedHostException{
		JenkinsCrawler crawler = loadTestPage(1);
		
		assertTrue(crawler.isUpdateAvailable(getDateDelta(crawler, -1000000), null));
	}
	
	@Test
	public void testIsUpdateAvailableForOldBuild() throws Exception {
		JenkinsCrawler crawler = loadTestPage(1);
		
		assertFalse(crawler.isUpdateAvailable(getDateDelta(crawler, 1000000), null));
	}
	
	@Test
	public void testIsUpdateAvailableForFailedBuild() throws Exception {
		JenkinsCrawler crawler = loadTestPage(2);
		
		assertFalse(crawler.isUpdateAvailable(getDateDelta(crawler, -1000000), null));
	}
	
	@Test
	public void testIsUpdateAvailableForFailedOldBuild() throws Exception {
		JenkinsCrawler crawler = loadTestPage(2);
		
		assertFalse(crawler.isUpdateAvailable(getDateDelta(crawler, +1000000), null));
	}

}
