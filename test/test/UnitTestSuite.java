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
   TestZipManager.class,
   TestModManager.class,
   TestModStateManager.class,
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
	
	public static Path getTempDir(String name) {
		try {
			Path path =  Files.createTempDirectory(name);
			path.toFile().deleteOnExit();
			return path;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
	}
	
	public static Path getTempFile(String name, String suffix) {
		try {
			Path file =  Files.createTempFile(name, suffix);
			file.toFile().deleteOnExit();
			return file;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
	}	
}