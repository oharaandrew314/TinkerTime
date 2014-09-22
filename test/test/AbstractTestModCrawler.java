package test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import test.util.MockCrawlerFactory;
import test.util.ModStubs;
import aohara.tinkertime.models.Mod;

public abstract class AbstractTestModCrawler {
	
	protected void compare(
		ModStubs stub, Date updatedOn, String creator,
		String newestFile, String downloadLink, String imageLink
	) throws IOException {
		Mod actualMod = new MockCrawlerFactory().getModCrawler(stub.url).createMod();
		
		Mod expectedMod = new Mod(
			stub.name,
			newestFile,
			creator,
			new URL(imageLink),
			stub.url,
			updatedOn
		);
		
		assertEquals(actualMod.getName(), expectedMod.getName());
		assertEquals(actualMod.getNewestFileName(), expectedMod.getNewestFileName());
		assertEquals(actualMod.getCreator(), expectedMod.getCreator());
		assertEquals(actualMod.getImageUrl(), expectedMod.getImageUrl());
		assertEquals(actualMod.getPageUrl(), expectedMod.getPageUrl());
		assertEquals(actualMod.getUpdatedOn().toString(), expectedMod.getUpdatedOn().toString());
	}
	
	protected Date getDate(int year, int month, int date){
		Calendar c = Calendar.getInstance();
		c.set(year, month, date, 0, 0, 0);
		return c.getTime();
	}

}
