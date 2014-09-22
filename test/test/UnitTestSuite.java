package test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.TestModManager;
import test.TestCurseCrawler;
import test.TestModStateManager;
import test.TestModStructure;


@RunWith(Suite.class)
@Suite.SuiteClasses({
   TestCurseCrawler.class,
   TestModManager.class,
   TestModStateManager.class,
   TestModStructure.class,
   TestMOduleManagerCrawler.class,
   TestGithubCrawler.class
})

public class UnitTestSuite {
	
	public static Path getTempDir(String name) {
		try {
			final Path path =  Files.createTempDirectory(name);
			 Runtime.getRuntime().addShutdownHook(new Thread() {
		            @Override
		            public void run() {
		                try {
							FileUtils.deleteDirectory(path.toFile());
						} catch (IOException e) {
							System.err.println("Could not delete " + path.toString());
						}
		            }
		        });
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