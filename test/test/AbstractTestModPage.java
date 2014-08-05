package test;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import test.util.ModLoader;
import aohara.tinkertime.models.pages.ModPage;

public abstract class AbstractTestModPage {
	
	protected void compare(
		String modName, Date updatedOn, String creator,
		String newestFile, String downloadLink, String imageLink
	){
		ModPage page = ModLoader.getHtmlPage(modName);
		
		assertEquals(modName, page.getName());
		assertEquals(updatedOn.toString(), page.getUpdatedOn().toString());
		assertEquals(creator, page.getCreator());
		assertEquals(newestFile, page.getNewestFileName());
		assertEquals(downloadLink, page.getDownloadLink().toString());
		assertEquals(imageLink, page.getImageUrl().toString());
		assertEquals(ModLoader.getUrl(modName), page.getPageUrl().toString());
	}
	
	protected Date getDate(int year, int month, int date){
		Calendar c = Calendar.getInstance();
		c.set(year, month, date, 0, 0, 0);
		return c.getTime();
	}

}
