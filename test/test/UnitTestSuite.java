package test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jsoup.Jsoup;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import aohara.tinkertime.config.Config;
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
			return Files.createTempDirectory(name);
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
	}
	
	public static Path getTempFile(String name, String suffix) {
		try {
			return Files.createTempFile(name, suffix);
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
	}
	
	public static class MockConfig extends Config {
		
		@Override
		public Path getKerbalPath(){
			return null;
		}
		
		@Override
		public Path getModsPath(){
			return null;
		}
	}
	
}