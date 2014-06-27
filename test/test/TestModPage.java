package test;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import aohara.tinkertime.models.ModPage;

public class TestModPage {
	
	private void compare(
		String modName, String modUrl, Date updatedOn, String creator,
		String newestFile, String downloadLink, String imageLink
	){
		ModPage page = UnitTestSuite.getModPage(modName, modUrl);
		
		assertEquals(modName, page.getName());
		assertEquals(updatedOn.toString(), page.getUpdatedOn().toString());
		assertEquals(creator, page.getCreator());
		assertEquals(newestFile, page.getNewestFile());
		assertEquals(downloadLink, page.getDownloadLink().toString());
		assertEquals(imageLink, page.getImageUrl().toString());
		assertEquals(modUrl, page.getPageUrl().toString());
	}
	
	private Date getDate(int year, int month, int date){
		Calendar c = Calendar.getInstance();
		c.set(year, month, date, 0, 0, 0);
		return c.getTime();
	}

	@Test
	public void testMechjeb() {		
		compare(
			"MechJeb",
			"http://www.curse.com/ksp-mods/kerbal/220221-mechjeb",
			getDate(2014, 4, 6),
			"r4m0n",
			"MechJeb2-2.2.1.0.zip",
			"http://addons.curse.cursecdn.com/files/2201/514/MechJeb2-2.2.1.0.zip",
			"http://media-curse.cursecdn.com/attachments/thumbnails/"
			+ "110/952/190/130/18b6dda728d709420f4b8959464e32ba.png"
		);
	}
	
	@Test
	public void testEngineer(){		
		compare(
			"Kerbal Engineer Redux",
			"http://www.curse.com/ksp-mods/kerbal/220285-kerbal-engineer-redux",
			getDate(2014, 4, 12),
			"cybutek",
			"Engineer_Redux_v0.6.2.4.zip",
			"http://addons.curse.cursecdn.com/files/2201/929/"
			+ "Engineer%20Redux%20v0.6.2.4.zip",
			"http://media-curse.cursecdn.com/attachments/thumbnails/111/144/"
			+ "190/130/46177821cf553bb4c8a15189f36d77f4.png"			
		);
	}

}
