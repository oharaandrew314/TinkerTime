package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import aohara.tinkertime.models.pages.ModuleManagerPage;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TestModuleManagerPage {
	
	private static ModuleManagerPage loadTestPage(int num) throws IOException{
		URL url = ModuleManagerPage.class.getClassLoader().getResource(
			String.format("test/res/ModuleManagerPage%d.json", num));
		try (Reader reader = new InputStreamReader(url.openStream())){
			JsonParser parser = new JsonParser();
			JsonObject obj = parser.parse(reader).getAsJsonObject();
			return ModuleManagerPage.loadPage(obj);
		}
	}
	
	private Date getDateDelta(ModuleManagerPage page, int delta){
		Date date = (Date) page.getUpdatedOn().clone();
		date.setTime(date.getTime() + delta);
		return date;
	}

	@Test
	public void testGetUpdatedOn() throws IOException {
		ModuleManagerPage page = loadTestPage(1);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(page.getUpdatedOn());
		
		assertEquals(2014, cal.get(Calendar.YEAR));
		assertEquals(Calendar.JULY, cal.get(Calendar.MONTH));
		assertEquals(19, cal.get(Calendar.DATE));
	}
	
	@Test
	public void testGetNewestFileName() throws IOException {
		ModuleManagerPage page = loadTestPage(1);
		
		assertEquals("ModuleManager.2.2.0.dll", page.getNewestFileName());
	}
	
	@Test
	public void testGetDownloadLink() throws IOException {
		ModuleManagerPage page = loadTestPage(1);
		
		String expectedUrl = (
			"https://ksp.sarbian.com/jenkins/job/ModuleManager/"
			+ "lastSuccessfulBuild/artifact/ModuleManager.2.2.0.dll");
		assertEquals(expectedUrl, page.getDownloadLink().toString());
	}
	
	@Test
	public void testIsUpdateAvailableForPassedBuild() throws IOException{
		ModuleManagerPage page = loadTestPage(1);
		
		assertTrue(page.isUpdateAvailable(getDateDelta(page, -1000000)));
	}
	
	@Test
	public void testIsUpdateAvailableForOldBuild() throws Exception {
		ModuleManagerPage page = loadTestPage(1);
		
		assertFalse(page.isUpdateAvailable(getDateDelta(page, 1000000)));
	}
	
	@Test
	public void testIsUpdateAvailableForFailedBuild() throws Exception {
		ModuleManagerPage page = loadTestPage(2);
		
		assertFalse(page.isUpdateAvailable(getDateDelta(page, -1000000)));
	}
	
	@Test
	public void testIsUpdateAvailableForFailedOldBuild() throws Exception {
		ModuleManagerPage page = loadTestPage(2);
		
		assertFalse(page.isUpdateAvailable(getDateDelta(page, +1000000)));
	}

}
