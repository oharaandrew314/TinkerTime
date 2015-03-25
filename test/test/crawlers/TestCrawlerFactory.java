package test.crawlers;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import test.util.MockHelper;
import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.CrawlerFactory;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.crawlers.CurseCrawler;
import aohara.tinkertime.crawlers.GithubHtmlCrawler;
import aohara.tinkertime.crawlers.GithubJsonCrawler;
import aohara.tinkertime.crawlers.KerbalStuffCrawler;

public class TestCrawlerFactory {
	
	private CrawlerFactory factory;
	
	@Before
	public void setUp(){
		factory = MockHelper.newCrawlerFactory();
	}
	
	private void test(String url, Class<? extends Crawler<?>> crawlerClass, boolean fallback) throws MalformedURLException, UnsupportedHostException{
		assertTrue(crawlerClass.isInstance(factory.getCrawler(new URL(url), fallback)));
	}

	@Test
	public void testCurseCom() throws MalformedURLException, UnsupportedHostException {
		test("http://curse.com/blahblahblab", CurseCrawler.class, false);
	}
	
	@Test
	public void testWwwCurseCom() throws MalformedURLException, UnsupportedHostException {
		test("http://www.curse.com/blah", CurseCrawler.class, false);
	}
	
	@Test
	public void testKerbalStuff() throws MalformedURLException, UnsupportedHostException{
		test("https://kerbalstuff.com/mod/9999999999", KerbalStuffCrawler.class, false);
	}
	
	@Test
	public void testBetaKerbalStuff() throws MalformedURLException, UnsupportedHostException{
		test("https://beta.kerbalstuff.com/mod/99999999998", KerbalStuffCrawler.class, false);
	}
	
	@Test
	public void testGithubCom() throws MalformedURLException, UnsupportedHostException{
		test("https://github.com/foo/bar", GithubJsonCrawler.class, false);
		factory.setFallbacksEnabled(true);
		test("https://github.com/foo/bar", GithubHtmlCrawler.class, true);
	}
	
	@Test
	public void testWwwGithubCom() throws MalformedURLException, UnsupportedHostException{
		test("https://www.github.com/bar/foo", GithubJsonCrawler.class, false);
		factory.setFallbacksEnabled(true);
		test("https://www.github.com/bar/foo", GithubHtmlCrawler.class, true);
	}

}
