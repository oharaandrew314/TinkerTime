package test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jsoup.Jsoup;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import aohara.tinkertime.models.ModPage;


@RunWith(Suite.class)
@Suite.SuiteClasses({
   TestModPage.class,
   TestZipManager.class
})

public class UnitTestSuite {
	
	public static ModPage getModPage(String modName){
		return getModPage(modName, "");
	}
	
	public static ModPage getModPage(String modName, String modUrl){
		try {
			return new ModPage(Jsoup.parse(
				Paths.get("test", "res", modName + ".html").toFile(),
				"UTF-8",
				modUrl
			));
		} catch (IOException ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	public static Path getTempPath(String name) {
		try {
			return Files.createTempDirectory(name);
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
	}
	
}