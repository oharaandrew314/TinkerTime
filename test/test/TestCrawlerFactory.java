package test;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.BeforeClass;
import org.junit.Test;

import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.CrawlerFactory;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.crawlers.CurseCrawler;
import aohara.tinkertime.crawlers.GithubCrawler;
import aohara.tinkertime.crawlers.KerbalStuffCrawler;

public class TestCrawlerFactory {
	
	private static CrawlerFactory factory;
	
	@BeforeClass
	public static void beforeClass(){
		factory = new CrawlerFactory();
	}
	
	private void test(String url, Class<? extends Crawler<?>> crawlerClass){
		try {
			assertTrue(crawlerClass.isInstance(factory.getCrawler(new URL(url))));
		} catch (MalformedURLException | UnsupportedHostException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testCurseCom() {
		test("http://curse.com/blahblahblab", CurseCrawler.class);
	}
	
	@Test
	public void testWwwCurseCom() {
		test("http://www.curse.com/blah", CurseCrawler.class);
	}
	
	@Test
	public void testKerbalStuff(){
		test("https://kerbalstuff.com/mod/239/Part%20Search", KerbalStuffCrawler.class);
	}
	
	@Test
	public void testBetaKerbalStuff(){
		test("https://beta.kerbalstuff.com/mod/239/Part%20Search", KerbalStuffCrawler.class);
	}
	
	@Test
	public void testGithubCom(){
		test("https://github.com/ferram4/Ferram-Aerospace-Research", GithubCrawler.class);
	}
	
	@Test
	public void testWwwGithibCom(){
		test("https://www.github.com/ferram4/Ferram-Aerospace-Research", GithubCrawler.class);
	}

}
