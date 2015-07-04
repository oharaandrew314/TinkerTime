package io.andrewohara.tinkertime.io.crawlers;

import static org.junit.Assert.assertEquals;
import io.andrewohara.tinkertime.io.crawlers.pageLoaders.JsonLoader;
import io.andrewohara.tinkertime.io.crawlers.pageLoaders.PageLoader;
import io.andrewohara.tinkertime.models.Installation.InvalidGameDataPathException;
import io.andrewohara.tinkertime.models.mod.Mod;
import io.andrewohara.tinkertime.models.mod.ModUpdateData;
import io.andrewohara.tinkertime.testUtil.ModTestFixtures;
import io.andrewohara.tinkertime.testUtil.StaticAssetSelector;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;

import com.google.gson.JsonElement;

public abstract class AbstractTestModCrawler {

	protected ModTestFixtures testFixtures;

	@Before
	public void setUp() throws URISyntaxException, InvalidGameDataPathException{
		testFixtures = new ModTestFixtures();
	}

	protected void testMod(Mod mod) throws IOException {
		// Setup Crawler
		Crawler<?> crawler = getCrawler(mod);
		crawler.setAssetSelector(new StaticAssetSelector());

		// Test Mod Update Data
		ModUpdateData updateData = crawler.getModUpdateData();
		assertEquals(mod.getName(), updateData.name);
		assertEquals(mod.getCreator(), updateData.creator);
		assertEquals(mod.getModVersion().getNormalVersion(), updateData.modVersion.getNormalVersion());
		assertEquals(mod.getSupportedVersion(), updateData.kspVersion);

		// Test Updated On
		Calendar expectedDate = Calendar.getInstance();
		expectedDate.setTime(mod.getUpdatedOn());

		Calendar actualDate = Calendar.getInstance();
		if (updateData.updatedOn != null){
			actualDate.setTime(updateData.updatedOn);
		}

		assertEquals(expectedDate.get(Calendar.YEAR), actualDate.get(Calendar.YEAR));
		assertEquals(expectedDate.get(Calendar.MONTH), actualDate.get(Calendar.MONTH));
		assertEquals(expectedDate.get(Calendar.DATE), actualDate.get(Calendar.DATE));
	}

	protected Date getDate(int year, int month, int date){
		Calendar c = Calendar.getInstance();
		c.set(year, month, date, 0, 0, 0);
		return c.getTime();
	}

	protected abstract Crawler<?> getCrawler(Mod mod);

	private String urlToPath(URL url){
		return url.toString().split("://")[1].replace("/", "-");
	}

	protected PageLoader<Document> getDocLoader(){
		return new PageLoader<Document>(){
			@Override
			public Document getPage(URL url) throws IOException {
				String resourceName = "html/" + urlToPath(url);
				try(InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName)){
					return Jsoup.parse(is, null, url.toString());
				} catch (NullPointerException e){
					throw new RuntimeException("Error opening stream: " + url.toString());
				}
			}

			@Override
			protected Document loadPage(URL url) throws IOException {
				return null;
			}
		};
	}

	protected PageLoader<JsonElement> getJsonLoader(){
		return new JsonLoader(){
			@Override
			public JsonElement getPage(URL url) throws IOException {
				String resourceName = "json/" + urlToPath(url);
				URL resourceUrl = getClass().getClassLoader().getResource(resourceName);
				return super.loadPage(resourceUrl);
			}
		};
	}
}
