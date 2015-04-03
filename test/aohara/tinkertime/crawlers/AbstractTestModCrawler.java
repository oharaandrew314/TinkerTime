package aohara.tinkertime.crawlers;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import aohara.tinkertime.crawlers.Crawler;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.models.Mod;
import aohara.tinkertime.testutil.ModStubs;
import aohara.tinkertime.testutil.ResourceLoader;

public abstract class AbstractTestModCrawler {
	
	protected void compare(
		ModStubs stub, String id, Date updatedOn, String creator,
		String newestFile, String downloadLink, String imageLink, String kspVersion,
		String version, boolean fallback
	) throws IOException, UnsupportedHostException {
		Crawler<?> crawler = ResourceLoader.loadCrawler(stub, fallback);
		
		Mod actualMod = crawler.call();
		assertEquals(id, actualMod.id);
		assertEquals(stub.name, actualMod.name);
		assertEquals(newestFile, actualMod.newestFileName);
		assertEquals(creator, actualMod.creator);
		assertEquals(newestFile, actualMod.newestFileName);
		assertEquals(stub.url, actualMod.pageUrl);
		assertEquals(version, actualMod.getVersion() != null ? actualMod.getVersion().toString() : null);
		
		URL imageUrl = crawler.getImageUrl();
		assertEquals(imageLink, imageUrl != null ? imageUrl.toString() : null);

		Calendar expectedDate = Calendar.getInstance();
		expectedDate.setTime(updatedOn);
		
		Calendar actualDate = Calendar.getInstance();
		actualDate.setTime(actualMod.updatedOn);
		
		assertEquals(expectedDate.get(Calendar.YEAR), actualDate.get(Calendar.YEAR));
		assertEquals(expectedDate.get(Calendar.MONTH), actualDate.get(Calendar.MONTH));
		assertEquals(expectedDate.get(Calendar.DATE), actualDate.get(Calendar.DATE));
	}
	
	protected void compare(
		ModStubs stub, String id, Date updatedOn, String creator,
		String newestFile, String downloadLink, String imageLink,
		String kspVersion, String version
	) throws IOException, UnsupportedHostException {
		compare(stub, id, updatedOn, creator, newestFile, downloadLink, imageLink, kspVersion, version, false);
	}
	
	protected Date getDate(int year, int month, int date){
		Calendar c = Calendar.getInstance();
		c.set(year, month, date, 0, 0, 0);
		return c.getTime();
	}

}
