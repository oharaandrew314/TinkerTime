package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import test.util.ModLoader;
import test.util.ModStubs;
import aohara.tinkertime.crawlers.CrawlerFactory.UnsupportedHostException;
import aohara.tinkertime.models.Mod;

public abstract class AbstractTestModCrawler {
	
	protected void compare(
		ModStubs stub, String id, Date updatedOn, String creator,
		String newestFile, String downloadLink, String imageLink, String supportedVersion
	) throws IOException, UnsupportedHostException {
		Mod actualMod = ModLoader.loadMod(stub);
		
		Mod expectedMod = new Mod(
			id,
			stub.name,
			newestFile,
			creator,
			new URL(imageLink),
			stub.url,
			updatedOn,
			supportedVersion
		);
		
		assertEquals(expectedMod.getName(), actualMod.getName());
		assertEquals(expectedMod.getNewestFileName(), actualMod.getNewestFileName());
		assertEquals(expectedMod.getCreator(), actualMod.getCreator());
		assertEquals(expectedMod.getImageUrl(), actualMod.getImageUrl());
		assertEquals(expectedMod.getPageUrl(), actualMod.getPageUrl());
		assertTrue(
			expectedMod.getUpdatedOn().compareTo(actualMod.getUpdatedOn()) < 100
		);
	}
	
	protected Date getDate(int year, int month, int date){
		Calendar c = Calendar.getInstance();
		c.set(year, month, date, 0, 0, 0);
		return c.getTime();
	}

}
