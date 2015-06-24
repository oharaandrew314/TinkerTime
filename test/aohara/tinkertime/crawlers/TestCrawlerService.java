package aohara.tinkertime.crawlers;

import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.modules.TestModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class TestCrawlerService {
	
	private static final Injector injector = Guice.createInjector(new TestModule());
	
	private CrawlerFactory factory; 
	
	@Before
	public void setUp(){
		factory = injector.getInstance(CrawlerFactory.class);
	}
	
	private void test(String url, Class<? extends Crawler<?>> crawlerClass, boolean fallback) throws MalformedURLException, UnsupportedHostException{
		assertTrue(crawlerClass.isInstance(factory.getCrawler(new URL(url))));
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
		test("https://github.com/foo/bar", GithubCrawler.class, false);
	}
	
	@Test
	public void testWwwGithubCom() throws MalformedURLException, UnsupportedHostException{
		test("https://www.github.com/bar/foo", GithubCrawler.class, false);
	}

}
